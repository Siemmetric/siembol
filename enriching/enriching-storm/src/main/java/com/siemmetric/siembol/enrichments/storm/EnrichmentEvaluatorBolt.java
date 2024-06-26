package com.siemmetric.siembol.enrichments.storm;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.siemmetric.siembol.common.constants.SiembolConstants;
import com.siemmetric.siembol.common.error.ErrorMessage;
import com.siemmetric.siembol.common.error.ErrorType;
import com.siemmetric.siembol.common.metrics.SiembolMetrics;
import com.siemmetric.siembol.common.metrics.SiembolMetricsRegistrar;
import com.siemmetric.siembol.common.metrics.storm.StormMetricsRegistrarFactory;
import com.siemmetric.siembol.common.metrics.storm.StormMetricsRegistrarFactoryImpl;
import com.siemmetric.siembol.common.model.ZooKeeperAttributesDto;
import com.siemmetric.siembol.common.zookeeper.ZooKeeperConnectorFactory;
import com.siemmetric.siembol.common.zookeeper.ZooKeeperConnector;
import com.siemmetric.siembol.common.zookeeper.ZooKeeperConnectorFactoryImpl;
import com.siemmetric.siembol.enrichments.common.EnrichmentResult;
import com.siemmetric.siembol.enrichments.compiler.EnrichmentCompilerImpl;
import com.siemmetric.siembol.enrichments.evaluation.EnrichmentEvaluator;
import com.siemmetric.siembol.enrichments.storm.common.EnrichmentTuples;
import com.siemmetric.siembol.enrichments.storm.common.EnrichmentCommands;
import com.siemmetric.siembol.enrichments.storm.common.EnrichmentExceptions;
import com.siemmetric.siembol.common.model.StormEnrichmentAttributesDto;

import java.lang.invoke.MethodHandles;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static com.siemmetric.siembol.enrichments.common.EnrichmentResult.StatusCode.OK;
/**
 * An object for integration of an enrichment evaluator into a storm bolt
 *
 * <p>This class extends a Storm BaseRichBolt class to implement a Storm bolt, that
 *  computes enrichment commands for a log by evaluating enrichment rules cached in the ZooKeeper.
 *  It watches for the enrichment rules update in ZooKeeper and
 *  updates the enrichment evaluator without needing to restart the topology or the bolt.
 *  It emits the event on input, the enrichment commands and exceptions after processing.
 *
 * @author Marian Novotny
 *
 * @see EnrichmentEvaluator
 * @see ZooKeeperConnector
 *
 */
public class EnrichmentEvaluatorBolt extends BaseRichBolt {
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private static final String INIT_EXCEPTION_MSG_FORMAT = "Enriching rule engine exception: %s during initialising alerts engine";
    private static final String UPDATE_EXCEPTION_LOG = "Exception during enriching rule engine update: {}";
    private static final String ENGINE_INIT_MESSAGE = "Enriching rule engine exception: Engine initialisation error";
    private static final String ENGINE_INIT_START = "Enriching rule engine initialisation start";
    private static final String ENGINE_INIT_COMPLETED = "Enriching rule engine initialisation completed";
    private static final String ENGINE_UPDATE_START = "Enriching rule engine update start";
    private static final String ENGINE_UPDATE_COMPLETED = "Enriching rule engine update completed";
    private static final String ENGINE_UPDATE_TRY_MSG_FORMAT = "Enriching rule engine is trying to update the rules: {}";
    private static final String EXCEPTION_RULE_EVALUATION = "Exception during enriching rule evaluation: {}";

    protected static final String COMPILER_EXCEPTION_MSG_FORMAT = "Exception during enriching rules compilation: %s";
    protected final AtomicReference<EnrichmentEvaluator> enrichmentEvaluator = new AtomicReference<>();

    private SiembolMetricsRegistrar metricsRegistrar;
    private OutputCollector collector;
    private ZooKeeperConnector zooKeeperConnector;
    private final ZooKeeperAttributesDto zooKeeperAttributes;
    private final ZooKeeperConnectorFactory zooKeeperConnectorFactory;
    private final StormMetricsRegistrarFactory metricsFactory;

    EnrichmentEvaluatorBolt(StormEnrichmentAttributesDto attributes,
                            ZooKeeperConnectorFactory zooKeeperConnectorFactory,
                            StormMetricsRegistrarFactory metricsFactory) {
        this.zooKeeperAttributes = attributes.getEnrichingRulesZookeperAttributes();
        this.zooKeeperConnectorFactory = zooKeeperConnectorFactory;
        this.metricsFactory = metricsFactory;
    }

    public EnrichmentEvaluatorBolt(StormEnrichmentAttributesDto attributes) {
        this(attributes, new ZooKeeperConnectorFactoryImpl(), new StormMetricsRegistrarFactoryImpl());
    }

    @SuppressWarnings("rawtypes")
    @Override
    public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
        this.collector = outputCollector;
        try {
            LOG.info(ENGINE_INIT_START);
            zooKeeperConnector = zooKeeperConnectorFactory.createZookeeperConnector(zooKeeperAttributes);
            metricsRegistrar = metricsFactory.createSiembolMetricsRegistrar(topologyContext);

            updateRules();
            if (enrichmentEvaluator.get() == null) {
                throw new IllegalStateException(ENGINE_INIT_MESSAGE);
            }

            zooKeeperConnector.addCacheListener(this::updateRules);
            LOG.info(ENGINE_INIT_COMPLETED);
        } catch (Exception e) {
            String msg = String.format(INIT_EXCEPTION_MSG_FORMAT, ExceptionUtils.getStackTrace(e));
            LOG.error(msg);
            throw new IllegalStateException(msg);
        }
    }

    private void updateRules() {
        try {
            LOG.info(ENGINE_UPDATE_START);

            String rules = zooKeeperConnector.getData();
            LOG.info(ENGINE_UPDATE_TRY_MSG_FORMAT, StringUtils.left(rules, SiembolConstants.MAX_SIZE_CONFIG_UPDATE_LOG));
            
            EnrichmentEvaluator engine = getEnrichmentEvaluator(rules);
            enrichmentEvaluator.set(engine);
            metricsRegistrar.registerCounter(SiembolMetrics.ENRICHMENT_RULES_UPDATE.getMetricName()).increment();
            LOG.info(ENGINE_UPDATE_COMPLETED);
        } catch (Exception e) {
            metricsRegistrar.registerCounter(SiembolMetrics.ENRICHMENT_RULES_ERROR_UPDATE.getMetricName()).increment();
            LOG.error(UPDATE_EXCEPTION_LOG, ExceptionUtils.getStackTrace(e));
        }
    }

    private EnrichmentEvaluator getEnrichmentEvaluator(String rules) {
        try {
            EnrichmentResult engineResult =  EnrichmentCompilerImpl.createEnrichmentCompiler().compile(rules);
            if (engineResult.getStatusCode() != OK) {
                String errorMsg = String.format(COMPILER_EXCEPTION_MSG_FORMAT,
                        engineResult.getAttributes().getMessage());
                LOG.error(errorMsg);
                throw new IllegalStateException(errorMsg);
            }
            return engineResult.getAttributes().getRuleEvaluator();
        } catch (Exception e) {
            String errorMsg = String.format(COMPILER_EXCEPTION_MSG_FORMAT,
                    ExceptionUtils.getStackTrace(e));
            LOG.error(errorMsg);
            throw new IllegalStateException(errorMsg);
        }
    }

    @Override
    public void execute(Tuple tuple) {
        EnrichmentEvaluator currentEvaluator = enrichmentEvaluator.get();
        String event = tuple.getStringByField(EnrichmentTuples.EVENT.toString());
        EnrichmentCommands commands = new EnrichmentCommands();
        EnrichmentExceptions exceptions = new EnrichmentExceptions();

        try {
            EnrichmentResult ret = currentEvaluator.evaluate(event);
            if (ret.getStatusCode() == OK) {
                if (ret.getAttributes().getEnrichmentCommands() != null) {
                    commands.addAll(ret.getAttributes().getEnrichmentCommands());
                }
            } else {
                if (ret.getAttributes().getMessage() != null) {
                    exceptions.add(ret.getAttributes().getMessage());
                }
            }
        } catch (Exception e) {
            LOG.error(EXCEPTION_RULE_EVALUATION, ExceptionUtils.getStackTrace(e));
            exceptions.add(ErrorMessage.createErrorMessage(e, ErrorType.ENRICHMENT_ERROR).toString());
        }

        collector.emit(tuple, new Values(event, commands, exceptions));
        collector.ack(tuple);
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields(EnrichmentTuples.EVENT.toString(),
                EnrichmentTuples.COMMANDS.toString(),
                EnrichmentTuples.EXCEPTIONS.toString()));
    }
}
