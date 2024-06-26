package com.siemmetric.siembol.configeditor.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.ReactiveHealthIndicator;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import com.siemmetric.siembol.configeditor.serviceaggregator.ServiceAggregator;

@Component
public class ConfigStoreHealthIndicator implements ReactiveHealthIndicator {
    @Autowired
    private ServiceAggregator serviceAggregator;

    @Override
    public Mono<Health> health() {
        return checkDownstreamServiceHealth().onErrorResume(
                ex -> Mono.just(new Health.Builder().down(ex).build()));
    }

    private Mono<Health> checkDownstreamServiceHealth() {
        return Mono.just(serviceAggregator.checkConfigStoreServicesHealth());
    }
}

