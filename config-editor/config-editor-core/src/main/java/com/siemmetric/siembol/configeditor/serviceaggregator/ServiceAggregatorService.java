package com.siemmetric.siembol.configeditor.serviceaggregator;

import com.siemmetric.siembol.configeditor.common.ConfigSchemaService;
import com.siemmetric.siembol.common.constants.ServiceType;
import com.siemmetric.siembol.configeditor.configstore.ConfigStore;

import static com.siemmetric.siembol.configeditor.model.ConfigEditorResult.StatusCode.OK;
/**
 * An object that represents a service used in the service aggregator
 *
 * <p>This class represents a service used in the service aggregator.
 *
 * @author  Marian Novotny
 */
public class ServiceAggregatorService {
    private final String name;
    private final ServiceType type;
    private final ConfigStore configStore;
    private final ConfigSchemaService configSchemaService;

    public ServiceAggregatorService(String name,
                                    ServiceType type,
                                    ConfigStore configStore,
                                    ConfigSchemaService configSchemaService) {
        this.name = name;
        this.type = type;
        this.configStore = configStore;
        this.configSchemaService = configSchemaService;
    }

    public ServiceType getType() {
        return type;
    }

    public ConfigStore getConfigStore() {
        return configStore;
    }

    public ConfigSchemaService getConfigSchemaService() {
        return configSchemaService;
    }

    public boolean supportsAdminConfiguration() {
        return configSchemaService.getAdminConfigurationSchema().getStatusCode() == OK;
    }

    public String getName() {
        return name;
    }
}
