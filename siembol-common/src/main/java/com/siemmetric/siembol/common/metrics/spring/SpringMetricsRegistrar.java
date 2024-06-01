package com.siemmetric.siembol.common.metrics.spring;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import com.siemmetric.siembol.common.metrics.SiembolCounter;
import com.siemmetric.siembol.common.metrics.SiembolGauge;
import com.siemmetric.siembol.common.metrics.SiembolMetricsRegistrar;
/**
 * An object for registering metrics in Spring
 *
 * <p>This class implements SiembolMetricsRegistrar interface, and it is used in Siembol Spring Boot projects.
 *
 * @author  Marian Novotny
 * @see SiembolMetricsRegistrar
 *
 */
public class SpringMetricsRegistrar implements SiembolMetricsRegistrar {
    private final MeterRegistry registry;

    public SpringMetricsRegistrar(MeterRegistry registry) {
        this.registry = registry;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SiembolCounter registerCounter(String name) {
        return new SpringCounter(Counter.builder(name).register(registry));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SiembolGauge registerGauge(String name) {
        var gauge = new SiembolGauge();
        Gauge
                .builder(name, gauge::getValue)
                .register(registry);

        return gauge;
    }
}
