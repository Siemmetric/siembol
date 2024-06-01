package com.siemmetric.siembol.response.evaluators.matching;

import com.siemmetric.siembol.alerts.common.EvaluationResult;
import com.siemmetric.siembol.alerts.engine.IsInSetMatcher;
import com.siemmetric.siembol.alerts.engine.RegexMatcher;
import com.siemmetric.siembol.alerts.engine.BasicMatcher;
import com.siemmetric.siembol.response.common.Evaluable;
import com.siemmetric.siembol.response.common.RespondingResult;
import com.siemmetric.siembol.response.common.ResponseAlert;
import com.siemmetric.siembol.response.model.MatcherDto;
import com.siemmetric.siembol.response.model.MatchingEvaluatorAttributesDto;
import com.siemmetric.siembol.response.model.MatchingEvaluatorResultDto;

import java.util.List;
import java.util.stream.Collectors;
/**
 * An object for evaluating response alerts
 *
 * <p>This class implements Evaluable interface, and it is used in a response rule.
 * The matching evaluator evaluates the alert by matching its underlying matchers.
 * It returns the evaluation result based on the matching result.
 *
 * @author  Marian Novotny
 * @see Evaluable
 */
public class MatchingEvaluator implements Evaluable {
    private static final String EMPTY_MATCHERS = "Empty matchers in matching evaluator";
    private final List<BasicMatcher> matchers;
    private final MatchingEvaluatorResultDto matchingResult;

    public MatchingEvaluator(MatchingEvaluatorAttributesDto attributesDto) {
        matchers = attributesDto.getMatchers().stream()
                .filter(MatcherDto::isEnabled)
                .map(this::createMatcher)
                .collect(Collectors.toList());
        if (matchers.isEmpty()) {
            throw new IllegalArgumentException(EMPTY_MATCHERS);
        }

        matchingResult = attributesDto.getEvaluationResult();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RespondingResult evaluate(ResponseAlert alert) {
        ResponseAlert current = (ResponseAlert)alert.clone();
        for (BasicMatcher matcher : matchers) {
            EvaluationResult result = matcher.match(current);
            if (result == EvaluationResult.NO_MATCH) {
                return RespondingResult.fromEvaluationResult(
                        matchingResult.computeFromEvaluationResult(EvaluationResult.NO_MATCH), alert);
            }
        }
        return RespondingResult.fromEvaluationResult(
                matchingResult.computeFromEvaluationResult(EvaluationResult.MATCH), current);
    }

    private BasicMatcher createMatcher(MatcherDto matcherDto) {
        switch (matcherDto.getType()) {
            case REGEX_MATCH:
                return RegexMatcher
                        .builder()
                        .pattern(matcherDto.getData())
                        .fieldName(matcherDto.getField())
                        .isNegated(matcherDto.getNegated())
                        .build();
            case IS_IN_SET:
                return IsInSetMatcher
                        .builder()
                        .data(matcherDto.getData())
                        .isCaseInsensitiveCompare(matcherDto.getCaseInsensitiveCompare())
                        .fieldName(matcherDto.getField())
                        .isNegated(matcherDto.getNegated())
                        .build();
        }

        throw new UnsupportedOperationException();
    }
}
