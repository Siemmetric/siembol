package com.siemmetric.siembol.common.jsonschema;

import com.siemmetric.siembol.common.result.SiembolResult;
/**
 * An object for json schema validation
 *
 * <p>This interface is used for representing a json schema validator.
 *
 * @author  Marian Novotny
 *
 */
public interface JsonSchemaValidator {
    /**
     * Gets json schema from the validator
     * @return siembol result with the json schema
     */
    SiembolResult getJsonSchema();

    /**
     * Validates a json string against the json schema
     * @param json a string for validation
     * @return siembol result with OK status code is the string is valid json, otherwise
     *         the result with ERROR status code.
     */
    SiembolResult validate(String json);
}
