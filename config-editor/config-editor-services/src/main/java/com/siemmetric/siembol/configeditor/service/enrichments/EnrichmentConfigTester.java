package com.siemmetric.siembol.configeditor.service.enrichments;

import com.siemmetric.siembol.common.jsonschema.SiembolJsonSchemaValidator;
import com.siemmetric.siembol.configeditor.common.ConfigTesterBase;
import com.siemmetric.siembol.configeditor.common.ConfigTesterFlag;
import com.siemmetric.siembol.configeditor.model.ConfigEditorResult;
import com.siemmetric.siembol.enrichments.compiler.EnrichmentCompiler;

import java.util.EnumSet;

import static com.siemmetric.siembol.configeditor.service.enrichments.EnrichmentSchemaService.fromEnrichmentResult;

public class EnrichmentConfigTester extends ConfigTesterBase<EnrichmentCompiler> {
    public EnrichmentConfigTester(SiembolJsonSchemaValidator testValidator,
                                  String testSchema,
                                  EnrichmentCompiler compiler) {
        super(testValidator, testSchema, compiler);
    }

    @Override
    public ConfigEditorResult testConfiguration(String configuration, String testSpecification) {
        return fromEnrichmentResult(testProvider.testConfiguration(configuration, testSpecification));
    }

    @Override
    public ConfigEditorResult testConfigurations(String configurations, String testSpecification) {
        return fromEnrichmentResult(testProvider.testConfigurations(configurations, testSpecification));
    }

    @Override
    public EnumSet<ConfigTesterFlag> getFlags() {
        return EnumSet.of(ConfigTesterFlag.CONFIG_TESTING,
                ConfigTesterFlag.TEST_CASE_TESTING);
    }
}
