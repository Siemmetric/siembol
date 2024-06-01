package com.siemmetric.siembol.response.evaluators.matching;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.siemmetric.siembol.common.jsonschema.SiembolJsonSchemaValidator;
import com.siemmetric.siembol.common.result.SiembolResult;
import com.siemmetric.siembol.response.common.ProvidedEvaluators;
import com.siemmetric.siembol.response.common.RespondingEvaluatorFactory;
import com.siemmetric.siembol.response.common.RespondingResult;
import com.siemmetric.siembol.response.model.MatchingEvaluatorAttributesDto;
/**
 * An object for creating a matching evaluator
 *
 * <p>This class implements RespondingEvaluatorFactory interface.
 * It is for creating a matching evaluator and providing metadata such as a type and attributes schema.
 * The matching evaluator evaluates the alert by matching its underlying matchers.
 * It returns the evaluation result based on the matching result.
 * Moreover, it provides the functionality for validating the evaluator attributes.
 *
 * @author  Marian Novotny
 * @see RespondingEvaluatorFactory
 * @see MatchingEvaluator
 */
public class MatchingEvaluatorFactory implements RespondingEvaluatorFactory {
    private static final ObjectReader JSON_ATTRIBUTES_READER = new ObjectMapper()
            .readerFor(MatchingEvaluatorAttributesDto.class);
    private final SiembolJsonSchemaValidator attributesSchema;

    public MatchingEvaluatorFactory() throws Exception {
        attributesSchema = new SiembolJsonSchemaValidator(MatchingEvaluatorAttributesDto.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RespondingResult createInstance(String attributes) {
        try {
            SiembolResult validationResult = attributesSchema.validate(attributes);
            if (validationResult.getStatusCode() !=  SiembolResult.StatusCode.OK) {
                return RespondingResult.fromSiembolResult(validationResult);
            }
            MatchingEvaluatorAttributesDto attributesDto = JSON_ATTRIBUTES_READER.readValue(attributes);
            MatchingEvaluator evaluator = new MatchingEvaluator(attributesDto);
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
        return RespondingResult.fromEvaluatorType(ProvidedEvaluators.MATCHING_EVALUATOR.toString());
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
