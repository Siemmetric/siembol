package com.siemmetric.siembol.response.engine;

import com.siemmetric.siembol.common.metrics.SiembolCounter;
import com.siemmetric.siembol.common.metrics.SiembolMetrics;
import com.siemmetric.siembol.common.metrics.SiembolMetricsRegistrar;
import com.siemmetric.siembol.common.testing.InactiveTestingLogger;
import com.siemmetric.siembol.common.testing.TestingLogger;
import com.siemmetric.siembol.response.common.*;

import java.util.List;

import static com.siemmetric.siembol.response.common.RespondingResult.StatusCode.OK;
/**
 * An object for evaluating response alerts using response rules
 *
 * <p>This class implements ResponseEngine interface.
 * It is used for evaluating response alerts and providing metadata about the rules.
 *
 * @author  Marian Novotny
 * @see ResponseEngine
 */
public class RulesEngine implements ResponseEngine {
    private static final String MISSING_ATTRIBUTES = "Missing response rule engine attributes";
    private static final String NO_RULE_MATCHES_THE_ALERT = "No rule matches the alert %s";

    private final List<? extends Evaluable> rules;
    private final SiembolCounter messagesCounter;
    private final SiembolCounter filtersCounter;
    private final SiembolCounter errorsCounter;
    private final SiembolCounter noMatchesCounter;
    private final TestingLogger logger;
    private final RespondingResultAttributes metadataAttributes;

    RulesEngine(Builder builder) {
        this.rules = builder.rules;
        this.logger = builder.logger;
        this.messagesCounter = builder.messagesCounter;
        this.filtersCounter = builder.filtersCounter;
        this.errorsCounter = builder.errorsCounter;
        this.noMatchesCounter = builder.noMatchesCounter;
        this.metadataAttributes = builder.metadataAttributes;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RespondingResult evaluate(ResponseAlert alert) {
        messagesCounter.increment();

        for (Evaluable rule: rules) {
            ResponseAlert current = (ResponseAlert)alert.clone();
            RespondingResult currentResult = rule.evaluate(current);
            if (currentResult.getStatusCode() != OK) {
                errorsCounter.increment();
                return currentResult;
            }

            if (currentResult.getAttributes().getResult() == ResponseEvaluationResult.FILTERED) {
                filtersCounter.increment();
                return currentResult;
            }

            if (currentResult.getAttributes().getResult() == ResponseEvaluationResult.MATCH) {
                return currentResult;
            }
        }

        noMatchesCounter.increment();
        RespondingResult result = RespondingResult.fromEvaluationResult(ResponseEvaluationResult.NO_MATCH, alert);
        String message = String.format(NO_RULE_MATCHES_THE_ALERT, alert.toString());
        logger.appendMessage(message);
        result.getAttributes().setMessage(message);
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RespondingResult getRulesMetadata() {
        return new RespondingResult(OK, metadataAttributes);
    }

    /**
     * A builder for a response rules engine
     *
     * @author  Marian Novotny
     */
    public static class Builder {
        private List<? extends Evaluable> rules;
        private TestingLogger logger = new InactiveTestingLogger();
        private SiembolMetricsRegistrar metricsRegistrar;
        private SiembolCounter messagesCounter;
        private SiembolCounter filtersCounter;
        private SiembolCounter errorsCounter;
        private SiembolCounter noMatchesCounter;
        private RespondingResultAttributes metadataAttributes;

        /**
         * Sets metrics registrar
         * @param metricsRegistrar for collecting the metrics
         * @return this builder
         */
        public Builder metricsRegistrar(SiembolMetricsRegistrar metricsRegistrar) {
            this.metricsRegistrar = metricsRegistrar;
            return this;
        }

        /**
         * Sets attributes with rules json schema
         * @param metadataAttributes with rules json schema
         * @return this builder
         */
        public Builder metadata(RespondingResultAttributes metadataAttributes) {
            this.metadataAttributes = metadataAttributes;
            return this;
        }

        /**
         * Sets the response rules
         *
         * @param rules response rules to be used in the engine
         * @return this builder
         */
        public Builder rules(List<? extends Evaluable> rules) {
            this.rules = rules;
            return this;
        }

        /**
         * Sets a testing logger
         *
         * @param logger a logger for testing
         * @return this builder
         */
        public Builder testingLogger(TestingLogger logger) {
            this.logger = logger;
            return this;
        }

        /**
         * Builds the rule engine
         *
         * @return the rules engine built from the builder
         */
        public RulesEngine build() {
            if (rules == null || rules.isEmpty()
                    || metricsRegistrar == null
                    || metadataAttributes == null) {
                throw new IllegalArgumentException(MISSING_ATTRIBUTES);
            }

            messagesCounter = metricsRegistrar
                    .registerCounter(SiembolMetrics.RESPONSE_ENGINE_PROCESSED_ALERTS.getMetricName());
            filtersCounter = metricsRegistrar
                    .registerCounter(SiembolMetrics.RESPONSE_ENGINE_FILTERED_ALERTS.getMetricName());
            errorsCounter = metricsRegistrar
                    .registerCounter(SiembolMetrics.RESPONSE_ENGINE_ERRORS.getMetricName());
            noMatchesCounter = metricsRegistrar
                    .registerCounter(SiembolMetrics.RESPONSE_ENGINE_NO_MATCHES.getMetricName());

            return new RulesEngine(this);
        }
    }
}
