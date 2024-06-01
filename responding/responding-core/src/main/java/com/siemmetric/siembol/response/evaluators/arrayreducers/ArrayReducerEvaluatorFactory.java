package com.siemmetric.siembol.response.evaluators.arrayreducers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.siemmetric.siembol.common.jsonschema.SiembolJsonSchemaValidator;
import com.siemmetric.siembol.common.result.SiembolResult;
import com.siemmetric.siembol.response.common.ProvidedEvaluators;
import com.siemmetric.siembol.response.common.RespondingEvaluatorFactory;
import com.siemmetric.siembol.response.common.RespondingResult;
import com.siemmetric.siembol.response.model.ArrayReducerEvaluatorAttributesDto;
/**
 * An object for creating an array reducer evaluator
 *
 * <p>This class implements RespondingEvaluatorFactory interface.
 * It is for creating an array reducer evaluator and providing metadata such as a type and attributes schema.
 * The array reducer evaluator reduces json arrays that can be created during evaluation of a response rule.
 * Moreover, it provides the functionality for validating the evaluator attributes.
 *
 * @author  Marian Novotny
 * @see RespondingEvaluatorFactory
 * @see ArrayReducerEvaluator
 */
public class ArrayReducerEvaluatorFactory implements RespondingEvaluatorFactory {
    private static final ObjectReader JSON_ATTRIBUTES_READER = new ObjectMapper()
            .readerFor(ArrayReducerEvaluatorAttributesDto.class);
    private final SiembolJsonSchemaValidator attributesSchema;

    public ArrayReducerEvaluatorFactory() throws Exception {
        attributesSchema = new SiembolJsonSchemaValidator(ArrayReducerEvaluatorAttributesDto.class);
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
            ArrayReducerEvaluatorAttributesDto attributesDto = JSON_ATTRIBUTES_READER.readValue(attributes);
            ArrayReducerEvaluator evaluator = new ArrayReducerEvaluator.Builder()
                    .arrayFieldName(attributesDto.getArrayField())
                    .delimiter(attributesDto.getFieldNameDelimiter())
                    .prefixName(attributesDto.getPrefixName())
                    .reducerType(attributesDto.getArrayReducerType())
                    .patternFilter(attributesDto.getFieldFilter().getIncludingFields(),
                            attributesDto.getFieldFilter().getExcludingFields())
                    .build();
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
        return RespondingResult.fromEvaluatorType(ProvidedEvaluators.ARRAY_REDUCER_EVALUATOR.toString());
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
