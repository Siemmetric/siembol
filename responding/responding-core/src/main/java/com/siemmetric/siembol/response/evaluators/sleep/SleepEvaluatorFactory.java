package com.siemmetric.siembol.response.evaluators.sleep;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.siemmetric.siembol.common.jsonschema.SiembolJsonSchemaValidator;
import com.siemmetric.siembol.common.result.SiembolResult;
import com.siemmetric.siembol.response.common.ProvidedEvaluators;
import com.siemmetric.siembol.response.common.RespondingEvaluatorFactory;
import com.siemmetric.siembol.response.common.RespondingResult;
import com.siemmetric.siembol.response.model.SleepEvaluatorAttributesDto;
/**
 * An object for creating a sleep evaluator
 *
 * <p>This class implements RespondingEvaluatorFactory interface.
 * It is for creating a sleep evaluator and providing metadata such as a type and attributes schema.
 * The sleep evaluator sleeps for certain time defined in the attributes. It is blocking the evaluation of the rule.
 * Moreover, it provides the functionality for validating the evaluator attributes.
 *
 * @author  Marian Novotny
 * @see RespondingEvaluatorFactory
 * @see SleepEvaluator
 */
public class SleepEvaluatorFactory implements RespondingEvaluatorFactory {
    private static final ObjectReader JSON_ATTRIBUTES_READER = new ObjectMapper()
            .readerFor(SleepEvaluatorAttributesDto.class);
    private final SiembolJsonSchemaValidator attributesSchema;

    public SleepEvaluatorFactory() throws Exception {
        attributesSchema = new SiembolJsonSchemaValidator(SleepEvaluatorAttributesDto.class);
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
            SleepEvaluatorAttributesDto attributesDto = JSON_ATTRIBUTES_READER.readValue(attributes);
            SleepEvaluator evaluator = new SleepEvaluator(attributesDto);
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
        return RespondingResult.fromEvaluatorType(ProvidedEvaluators.SLEEP_EVALUATOR.toString());
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
