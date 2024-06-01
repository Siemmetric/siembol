package com.siemmetric.siembol.response.evaluators.fixed;

import com.siemmetric.siembol.response.common.Evaluable;
import com.siemmetric.siembol.response.common.ResponseEvaluationResult;
import com.siemmetric.siembol.response.common.RespondingResult;
import com.siemmetric.siembol.response.common.ResponseAlert;
/**
 * An object for evaluating response alerts
 *
 * <p>This class implements Evaluable interface, and it is used in a response rule.
 * The fixed result evaluator returns always the same result from the attributes.
 *
 * @author  Marian Novotny
 * @see Evaluable
 */
public class FixedResultEvaluator implements Evaluable {
    private final ResponseEvaluationResult returnResult;
    public FixedResultEvaluator(ResponseEvaluationResult returnResult) {
        this.returnResult = returnResult;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RespondingResult evaluate(ResponseAlert alert) {
        return RespondingResult.fromEvaluationResult(returnResult, alert);
    }
}
