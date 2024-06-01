package com.siemmetric.siembol.response.stream.ruleservice;

import org.springframework.boot.actuate.health.Health;
import reactor.core.publisher.Mono;
import com.siemmetric.siembol.response.common.RespondingResult;
import com.siemmetric.siembol.response.common.RespondingResultAttributes;

import static com.siemmetric.siembol.response.common.RespondingResult.StatusCode.OK;

public class InactiveRulesService implements RulesService {
    private static String INFO_MESSAGE = "No rules service is loaded. The streaming rules service is inactive";
    @Override
    public RespondingResult getRulesMetadata() {
        RespondingResultAttributes attributes = new RespondingResultAttributes();
        attributes.setMessage(INFO_MESSAGE);
        return new RespondingResult(OK, attributes);
    }

    @Override
    public Mono<Health> checkHealth() {
        return Mono.just(Health.up().build());
    }
}
