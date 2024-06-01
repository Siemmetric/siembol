package com.siemmetric.siembol.configeditor.common;

import org.springframework.boot.actuate.health.Health;

public interface HealthCheckable {
    Health checkHealth();
}
