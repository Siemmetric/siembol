package com.siemmetric.siembol.deployment.storm.service;

import org.springframework.boot.actuate.health.Health;
import com.siemmetric.siembol.deployment.storm.model.TopologyManagerInfoDto;

public interface TopologyManagerService {
    void invokeSynchronise();
    TopologyManagerInfoDto getTopologyManagerInfo();
    Health checkHealth();
}
