package com.siemmetric.siembol.response.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.reinert.jjschema.Attributes;

import com.siemmetric.siembol.alerts.common.EvaluationResult;
import com.siemmetric.siembol.response.common.ResponseEvaluationResult;
/**
 * An enum for representing a matching evaluator result
 *
 * <p>This enum is used for json (de)serialisation of a matching evaluator result used in a matching evaluator.
 *
 * @author  Marian Novotny
 * @see com.fasterxml.jackson.annotation.JsonProperty
 * @see #MATCH
 * @see #FILTERED
 * @see #FILTERED_WHEN_NO_MATCH
 */
@Attributes(title = "matching evaluator result", description = "Result after matching")
public enum MatchingEvaluatorResultDto {
    @JsonProperty("match") MATCH("match"),
    @JsonProperty("filtered") FILTERED("filtered"),
    @JsonProperty("filtered_when_no_match") FILTERED_WHEN_NO_MATCH("filtered_when_no_match");

    private final String name;

    MatchingEvaluatorResultDto(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    public ResponseEvaluationResult computeFromEvaluationResult(EvaluationResult evaluationResult) {
        if (evaluationResult == EvaluationResult.NO_MATCH) {
            return this == FILTERED_WHEN_NO_MATCH
                    ? ResponseEvaluationResult.FILTERED
                    : ResponseEvaluationResult.NO_MATCH;
        }
        return this == FILTERED ? ResponseEvaluationResult.FILTERED : ResponseEvaluationResult.MATCH;
    }
}
