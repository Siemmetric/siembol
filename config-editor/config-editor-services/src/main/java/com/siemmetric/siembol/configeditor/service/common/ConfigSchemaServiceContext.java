package com.siemmetric.siembol.configeditor.service.common;

import com.siemmetric.siembol.common.jsonschema.SiembolJsonSchemaValidator;
import com.siemmetric.siembol.configeditor.common.ConfigImporter;
import com.siemmetric.siembol.configeditor.common.ConfigTester;

import java.util.*;

public class ConfigSchemaServiceContext {
    private String configSchema;
    private String testSchema;
    private String adminConfigSchema;
    private SiembolJsonSchemaValidator adminConfigValidator;
    private Map<String, ConfigImporter> configImporters = new HashMap<>();

    private List<ConfigTester> configTesters = new ArrayList<>();

    public String getConfigSchema() {
        return configSchema;
    }

    public void setConfigSchema(String configSchema) {
        this.configSchema = configSchema;
    }

    public String getTestSchema() {
        return testSchema;
    }

    public void setTestSchema(String testSchema) {
        this.testSchema = testSchema;
    }

    public String getAdminConfigSchema() {
        return adminConfigSchema;
    }

    public void setAdminConfigSchema(String adminConfigSchema) {
        this.adminConfigSchema = adminConfigSchema;
    }

    public SiembolJsonSchemaValidator getAdminConfigValidator() {
        return adminConfigValidator;
    }

    public void setAdminConfigValidator(SiembolJsonSchemaValidator adminConfigValidator) {
        this.adminConfigValidator = adminConfigValidator;
    }

    public Map<String, ConfigImporter> getConfigImporters() {
        return configImporters;
    }

    public void setConfigImporters(Map<String, ConfigImporter> configImporters) {
        this.configImporters = configImporters;
    }

    public List<ConfigTester> getConfigTesters() {
        return configTesters;
    }

    public void setConfigTesters(List<ConfigTester> configTesters) {
        this.configTesters = configTesters;
    }

}
