package com.siemmetric.siembol.response.evaluators.timeexclusion;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.siemmetric.siembol.common.jsonschema.SiembolJsonSchemaValidator;
import com.siemmetric.siembol.common.result.SiembolResult;
import com.siemmetric.siembol.response.common.ProvidedEvaluators;
import com.siemmetric.siembol.response.common.RespondingEvaluatorFactory;
import com.siemmetric.siembol.response.common.RespondingResult;
import com.siemmetric.siembol.response.model.TimeExclusionEvaluatorAttributesDto;
/**
 * An object for creating a time exclusion evaluator
 *
 * <p>This class implements RespondingEvaluatorFactory interface.
 * It is used for creating a time exclusion evaluator, and it provides metadata such as a type and attributes schema.
 * The time exclusion evaluator inspects a timestamp and evaluates an excluding pattern.
 * It filters the alert if the pattern matches.
 * Moreover, it provides the functionality for validating the evaluator attributes.
 *
 * @author  Marian Novotny
 * @see RespondingEvaluatorFactory
 * @see TimeExclusionEvaluator
 */
public class TimeExclusionEvaluatorFactory implements RespondingEvaluatorFactory {
    private static final ObjectReader JSON_ATTRIBUTES_READER = new ObjectMapper()
            .readerFor(TimeExclusionEvaluatorAttributesDto.class);
    private final SiembolJsonSchemaValidator attributesSchema;

    public TimeExclusionEvaluatorFactory() throws Exception {
        attributesSchema = new SiembolJsonSchemaValidator(TimeExclusionEvaluatorAttributesDto.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RespondingResult createInstance(String attributes) {
        try {
            SiembolResult validationResult = attributesSchema.validate(attributes);
            if (validationResult.getStatusCode() != SiembolResult.StatusCode.OK) {
                return RespondingResult.fromSiembolResult(validationResult);
            }
            TimeExclusionEvaluatorAttributesDto attributesDto = JSON_ATTRIBUTES_READER.readValue(attributes);
            var evaluator = new TimeExclusionEvaluator(attributesDto);
            return RespondingResult.fromEvaluator(evaluator);
        } catch (Exception e) {
            return RespondingResult.fromException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RespondingResult getType() {
        return RespondingResult.fromEvaluatorType(ProvidedEvaluators.TIME_EXCLUSION_EVALUATOR.toString());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RespondingResult getAttributesJsonSchema() {
        return RespondingResult.fromAttributesSchema(
                attributesSchema.getJsonSchema().getAttributes().getJsonSchema());
    }
}
