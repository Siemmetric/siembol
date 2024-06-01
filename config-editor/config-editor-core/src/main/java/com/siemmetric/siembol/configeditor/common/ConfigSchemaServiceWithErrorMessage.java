package com.siemmetric.siembol.configeditor.common;

import org.springframework.boot.actuate.health.Health;
import com.siemmetric.siembol.configeditor.model.ConfigEditorResult;
import com.siemmetric.siembol.configeditor.model.ErrorMessages;
import com.siemmetric.siembol.configeditor.model.ErrorResolutions;
import com.siemmetric.siembol.configeditor.model.ErrorTitles;

import java.util.Map;
import java.util.function.Supplier;
/**
 * An object for configuration schema service with enhanced error messages
 *
 * <p>This class implements ConfigSchemaService interface, and it extends ServiceWithErrorMessage class.
 * It enriches error messages on error.
 *
 * @author  Marian Novotny
 * @see ServiceWithErrorMessage
 * @see ConfigSchemaService
 */
public class ConfigSchemaServiceWithErrorMessage extends ServiceWithErrorMessage<ConfigSchemaService>
        implements ConfigSchemaService {

    public ConfigSchemaServiceWithErrorMessage(ConfigSchemaService service) {
        super(service);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ConfigEditorResult getSchema() {
        return service.getSchema();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ConfigEditorResult validateConfiguration(String configuration) {
        Supplier<ConfigEditorResult> fun = () -> service.validateConfiguration(configuration);
        return executeInternally(fun, ErrorTitles.VALIDATION_GENERIC.getTitle(),
                ErrorMessages.VALIDATION_GENERIC.getMessage(),
                ErrorResolutions.VALIDATION.getResolution());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ConfigEditorResult validateConfigurations(String configurations) {
        Supplier<ConfigEditorResult> fun = () -> service.validateConfigurations(configurations);
        return executeInternally(fun, ErrorTitles.VALIDATION_GENERIC.getTitle(),
                ErrorMessages.VALIDATION_GENERIC.getMessage(),
                ErrorResolutions.VALIDATION.getResolution());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, ConfigImporter> getConfigImporters() {
        return service.getConfigImporters();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ConfigEditorResult getConfigTesters() {
        return service.getConfigTesters();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ConfigTester getConfigTester(String name) {
        return service.getConfigTester(name);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Health checkHealth() {
        return service.checkHealth();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ConfigEditorResult getImporters() {
        return service.getImporters();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ConfigEditorResult importConfig(UserInfo user,
                                           String importerName,
                                           String importerAttributes,
                                           String configToImport) {
        Supplier<ConfigEditorResult> fun = () -> service.importConfig(user,
                importerName, importerAttributes, configToImport);
        return executeInternally(fun, ErrorTitles.IMPORTING_CONFIG_GENERIC.getTitle(),
                ErrorMessages.GENERIC_CONFIG_IMPORTER.getMessage(importerName),
                ErrorResolutions.GENERIC_BAD_REQUEST.getResolution());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ConfigEditorResult getAdminConfigurationSchema() {
        return service.getAdminConfigurationSchema();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ConfigEditorResult validateAdminConfiguration(String configuration) {
        Supplier<ConfigEditorResult> fun = () -> service.validateAdminConfiguration(configuration);
        return executeInternally(fun, ErrorTitles.VALIDATION_GENERIC.getTitle(),
                ErrorMessages.VALIDATION_GENERIC.getMessage(),
                ErrorResolutions.VALIDATION.getResolution());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ConfigEditorResult getAdminConfigTopologyName(String configuration) {
        return service.getAdminConfigTopologyName(configuration);
    }
}
