package com.siemmetric.siembol.enrichments.storm;

import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.siemmetric.siembol.common.error.ErrorMessage;
import com.siemmetric.siembol.common.error.ErrorType;
import com.siemmetric.siembol.common.constants.SiembolMessageFields;
import com.siemmetric.siembol.common.storm.KafkaWriterMessage;
import com.siemmetric.siembol.common.storm.KafkaWriterMessages;
import com.siemmetric.siembol.common.storm.SiembolMetricsCounters;
import com.siemmetric.siembol.enrichments.evaluation.EnrichmentEvaluatorLibrary;
import com.siemmetric.siembol.enrichments.storm.common.EnrichmentTuples;
import com.siemmetric.siembol.enrichments.storm.common.EnrichmentPairs;
import com.siemmetric.siembol.enrichments.storm.common.EnrichmentExceptions;
import com.siemmetric.siembol.common.model.StormEnrichmentAttributesDto;

import java.lang.invoke.MethodHandles;
import java.util.Map;
import java.util.Optional;
/**
 * An object for integration of merging enrichment with an event into a storm bolt
 *
 * <p>This class extends a Storm BaseRichBolt class to implement a Storm bolt, that
 *  merges an event with enrichment computed by a table enrichment bolt.
 *  It also creates messages with a topic to be published to Kafka.
 *  It emits the messages to be written to Kafka and counters after processing.
 *
 * @author Marian Novotny
 */
public class EnrichmentMergerBolt extends BaseRichBolt {
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static final String INVALID_TYPE_IN_TUPLES = "Invalid type in tuple provided";
    private static final String MERGING_ERROR = "Unable to merge the event: {} with the enrichments : {}";
    private static final String EVENT_INFO_LOG = "The event after enrichments: {}";

    private OutputCollector collector;
    private final String outputTopic;
    private final String errorTopic;

    public EnrichmentMergerBolt(StormEnrichmentAttributesDto attributes) {
        this.outputTopic = attributes.getEnrichingOutputTopic();
        this.errorTopic = attributes.getEnrichingErrorTopic();
    }

    @SuppressWarnings("rawtypes")
    @Override
    public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
        collector = outputCollector;
    }

    @Override
    public void execute(Tuple tuple) {
        Object enrichmentsObj = tuple.getValueByField(EnrichmentTuples.ENRICHMENTS.toString());
        if (!(enrichmentsObj instanceof EnrichmentPairs)) {
            LOG.error(INVALID_TYPE_IN_TUPLES);
            throw new IllegalArgumentException(INVALID_TYPE_IN_TUPLES);
        }
        EnrichmentPairs enrichments = (EnrichmentPairs)enrichmentsObj;

        String event = tuple.getStringByField(EnrichmentTuples.EVENT.toString());

        Object exceptionsObj = tuple.getValueByField(EnrichmentTuples.EXCEPTIONS.toString());
        if (!(exceptionsObj instanceof EnrichmentExceptions)) {
            LOG.error(INVALID_TYPE_IN_TUPLES);
            throw new IllegalArgumentException(INVALID_TYPE_IN_TUPLES);
        }
        EnrichmentExceptions exceptions = (EnrichmentExceptions)exceptionsObj;

        Object countersObj = tuple.getValueByField(EnrichmentTuples.COUNTERS.toString());
        if (!(countersObj instanceof SiembolMetricsCounters)) {
            LOG.error(INVALID_TYPE_IN_TUPLES);
            throw new IllegalArgumentException(INVALID_TYPE_IN_TUPLES);
        }

        var counters = new SiembolMetricsCounters();
        counters.addAll((SiembolMetricsCounters)countersObj);

        try {
            event = EnrichmentEvaluatorLibrary.mergeEnrichments(event,
                    enrichments,
                    Optional.of(SiembolMessageFields.ENRICHING_TIME.toString()));
        } catch (Exception e) {
            LOG.error(MERGING_ERROR, event, enrichments);
            exceptions.add(ErrorMessage.createErrorMessage(e, ErrorType.ENRICHMENT_ERROR).toString());
        }

        LOG.debug(EVENT_INFO_LOG, event);
        KafkaWriterMessages messages = new KafkaWriterMessages();
        messages.add(new KafkaWriterMessage(outputTopic, event));
        exceptions.forEach(x -> messages.add(new KafkaWriterMessage(errorTopic, x)));
        collector.emit(tuple, new Values(messages, counters));
        collector.ack(tuple);
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields(EnrichmentTuples.KAFKA_MESSAGES.toString(), EnrichmentTuples.COUNTERS.toString()));
    }
}
