package com.siemmetric.siembol.configeditor.sync.service;

import org.springframework.boot.actuate.health.Health;
import com.siemmetric.siembol.configeditor.model.ConfigEditorResult;
import com.siemmetric.siembol.configeditor.sync.common.SynchronisationType;

import java.util.Arrays;
import java.util.List;

public interface SynchronisationService {
    default ConfigEditorResult synchroniseService(String serviceName, SynchronisationType syncType) {
        return synchroniseServices(Arrays.asList(serviceName), syncType);
    }

    ConfigEditorResult synchroniseServices(List<String> serviceNames, SynchronisationType syncType);

    ConfigEditorResult synchroniseAllServices(SynchronisationType syncType);

    Health checkHealth();
}
