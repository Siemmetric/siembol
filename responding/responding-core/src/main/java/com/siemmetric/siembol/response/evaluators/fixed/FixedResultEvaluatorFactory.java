package com.siemmetric.siembol.response.evaluators.fixed;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.siemmetric.siembol.common.jsonschema.SiembolJsonSchemaValidator;
import com.siemmetric.siembol.common.result.SiembolResult;
import com.siemmetric.siembol.response.common.ProvidedEvaluators;
import com.siemmetric.siembol.response.common.RespondingEvaluatorFactory;
import com.siemmetric.siembol.response.common.RespondingResult;
import com.siemmetric.siembol.response.model.FixedResultEvaluatorAttributesDto;
/**
 * An object for creating a fixed result evaluator
 *
 * <p>This class implements RespondingEvaluatorFactory interface.
 * It is for creating a fixed result evaluator and providing metadata such as a type and attributes schema.
 * The fixed result evaluator returns always the same result from the attributes.
 * Moreover, it provides the functionality for validating the evaluator attributes.
 *
 * @author  Marian Novotny
 * @see RespondingEvaluatorFactory
 * @see FixedResultEvaluator
 */
public class FixedResultEvaluatorFactory implements RespondingEvaluatorFactory {
    private static final ObjectReader JSON_ATTRIBUTES_READER = new ObjectMapper()
            .readerFor(FixedResultEvaluatorAttributesDto.class);
    private final SiembolJsonSchemaValidator attributesSchema;

    public FixedResultEvaluatorFactory() throws Exception {
        attributesSchema = new SiembolJsonSchemaValidator(FixedResultEvaluatorAttributesDto.class);
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
            FixedResultEvaluatorAttributesDto attributesDto = JSON_ATTRIBUTES_READER.readValue(attributes);
            FixedResultEvaluator evaluator = new FixedResultEvaluator(attributesDto.getResponseEvaluationResult());
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
        return RespondingResult.fromEvaluatorType(ProvidedEvaluators.FIXED_RESULT_EVALUATOR.toString());
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
