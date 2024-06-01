package com.siemmetric.siembol.configeditor.service.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.siemmetric.siembol.common.jsonschema.SiembolJsonSchemaValidator;
import com.siemmetric.siembol.configeditor.common.ConfigTesterBase;
import com.siemmetric.siembol.configeditor.common.ConfigTesterFlag;
import com.siemmetric.siembol.configeditor.model.ConfigEditorResult;
import com.siemmetric.siembol.response.common.RespondingResult;
import com.siemmetric.siembol.response.common.RespondingResultAttributes;
import com.siemmetric.siembol.response.compiler.RespondingCompilerImpl;

import java.util.EnumSet;

import static com.siemmetric.siembol.configeditor.service.response.ResponseSchemaService.fromRespondingResult;

public class ResponseConfigTester extends ConfigTesterBase<ResponseHttpProvider> {
    private static final ObjectWriter ATTRIBUTES_WRITER = new ObjectMapper()
            .setSerializationInclusion(JsonInclude.Include.NON_NULL)
            .writerFor(RespondingResultAttributes.class);

    public ResponseConfigTester(SiembolJsonSchemaValidator testValidator,
                                String testSchema,
                                ResponseHttpProvider compiler) {
        super(testValidator, testSchema, compiler);
    }

    @Override
    public ConfigEditorResult testConfiguration(String configuration, String testSpecification) {
        String rules = RespondingCompilerImpl.wrapRuleToRules(configuration);
        return testConfigurations(rules, testSpecification);
    }

    @Override
    public ConfigEditorResult testConfigurations(String configurations, String testSpecification) {
        try {
            RespondingResult result = testProvider.testRules(configurations, testSpecification);
            ConfigEditorResult configEditorResult = fromRespondingResult(result);
            if (configEditorResult.getStatusCode() == ConfigEditorResult.StatusCode.OK) {
                configEditorResult.getAttributes().setTestResultOutput(result.getAttributes().getMessage());
                result.getAttributes().setMessage(null);
                configEditorResult.getAttributes()
                        .setTestResultRawOutput(ATTRIBUTES_WRITER.writeValueAsString(result.getAttributes()));
            }
            return configEditorResult;
        } catch (Exception e) {
            return ConfigEditorResult.fromException(e);
        }
    }

    @Override
    public EnumSet<ConfigTesterFlag> getFlags() {
        return EnumSet.of(ConfigTesterFlag.CONFIG_TESTING,
                ConfigTesterFlag.RELEASE_TESTING);
    }
}
