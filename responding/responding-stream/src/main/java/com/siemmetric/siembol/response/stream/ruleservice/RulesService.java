package com.siemmetric.siembol.response.stream.ruleservice;

import org.springframework.boot.actuate.health.Health;
import reactor.core.publisher.Mono;
import com.siemmetric.siembol.response.common.RespondingResult;

public interface RulesService {
    RespondingResult getRulesMetadata();
    Mono<Health> checkHealth();
    default void close() {}
    default void initialise() {};
}
