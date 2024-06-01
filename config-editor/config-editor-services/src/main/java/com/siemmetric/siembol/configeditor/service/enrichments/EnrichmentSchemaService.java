package com.siemmetric.siembol.configeditor.service.enrichments;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.siemmetric.siembol.common.jsonschema.SiembolJsonSchemaValidator;
import com.siemmetric.siembol.common.model.testing.EnrichmentTestingSpecificationDto;
import com.siemmetric.siembol.configeditor.common.ConfigEditorUtils;
import com.siemmetric.siembol.configeditor.common.ConfigSchemaService;
import com.siemmetric.siembol.configeditor.model.ConfigEditorAttributes;
import com.siemmetric.siembol.configeditor.model.ConfigEditorResult;
import com.siemmetric.siembol.configeditor.model.ConfigEditorUiLayout;
import com.siemmetric.siembol.configeditor.service.common.ConfigSchemaServiceAbstract;
import com.siemmetric.siembol.configeditor.service.common.ConfigSchemaServiceContext;
import com.siemmetric.siembol.enrichments.common.EnrichmentResult;
import com.siemmetric.siembol.enrichments.compiler.EnrichmentCompiler;
import com.siemmetric.siembol.enrichments.compiler.EnrichmentCompilerImpl;
import com.siemmetric.siembol.common.model.StormEnrichmentAttributesDto;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Optional;

import static com.siemmetric.siembol.configeditor.model.ConfigEditorResult.StatusCode.OK;

public class EnrichmentSchemaService extends ConfigSchemaServiceAbstract {
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static final String INIT_ERROR = "Error during initialisation of enrichment rules and testing schema";
    private static final ObjectReader ADMIN_CONFIG_READER = new ObjectMapper()
            .readerFor(StormEnrichmentAttributesDto.class);

    private final EnrichmentCompiler compiler;

    EnrichmentSchemaService(EnrichmentCompiler compiler, ConfigSchemaServiceContext context) {
        super(context);
        this.compiler = compiler;
    }

    @Override
    public ConfigEditorResult validateConfiguration(String configuration) {
        return fromEnrichmentResult(compiler.validateConfiguration(configuration));
    }

    @Override
    public ConfigEditorResult validateConfigurations(String configurations) {
        return fromEnrichmentResult(compiler.validateConfigurations(configurations));
    }

    @Override
    public ConfigEditorResult getAdminConfigTopologyName(String configuration) {
        try {
            StormEnrichmentAttributesDto adminConfig = ADMIN_CONFIG_READER.readValue(configuration);
            ConfigEditorAttributes attributes = new ConfigEditorAttributes();
            attributes.setTopologyName(adminConfig.getTopologyName());
            return new ConfigEditorResult(OK, attributes);
        } catch (IOException e) {
            return ConfigEditorResult.fromException(e);
        }
    }

   public static ConfigEditorResult fromEnrichmentResult(EnrichmentResult enrichmentResult) {
        ConfigEditorAttributes attr = new ConfigEditorAttributes();
        attr.setMessage(enrichmentResult.getAttributes().getMessage());
        attr.setTestResultRawOutput(enrichmentResult.getAttributes().getTestRawResult());
        attr.setTestResultOutput(enrichmentResult.getAttributes().getTestResult());

        ConfigEditorResult.StatusCode statusCode =
                enrichmentResult.getStatusCode() == EnrichmentResult.StatusCode.OK
                        ? ConfigEditorResult.StatusCode.OK
                        : ConfigEditorResult.StatusCode.BAD_REQUEST;
        return new ConfigEditorResult(statusCode, attr);
    }

    public static ConfigSchemaService createEnrichmentsSchemaService(ConfigEditorUiLayout uiLayout) throws Exception {
        LOG.info("Initialising enrichment config schema service");
        ConfigSchemaServiceContext context = new ConfigSchemaServiceContext();
        EnrichmentCompiler compiler = EnrichmentCompilerImpl.createEnrichmentCompiler();
        String rulesSchema = compiler.getSchema().getAttributes().getRulesSchema();
        String testSchema = compiler.getTestSpecificationSchema().getAttributes().getTestSchema();

        Optional<String> rulesSchemaUi = ConfigEditorUtils.patchJsonSchema(rulesSchema, uiLayout.getConfigLayout());
        Optional<String> testSchemaUi = ConfigEditorUtils.patchJsonSchema(testSchema, uiLayout.getTestLayout());
        SiembolJsonSchemaValidator adminConfigValidator = new SiembolJsonSchemaValidator(
                StormEnrichmentAttributesDto.class);
        Optional<String> adminConfigSchemaUi = ConfigEditorUtils.patchJsonSchema(
                adminConfigValidator.getJsonSchema().getAttributes().getJsonSchema(),
                uiLayout.getAdminConfigLayout());

        if (!rulesSchemaUi.isPresent()
                || !testSchemaUi.isPresent()
                || !adminConfigSchemaUi.isPresent()) {
            LOG.error(INIT_ERROR);
            throw new IllegalArgumentException(INIT_ERROR);
        }

        context.setConfigSchema(rulesSchemaUi.get());
        context.setTestSchema(testSchemaUi.get());
        context.setAdminConfigSchema(adminConfigSchemaUi.get());
        context.setAdminConfigValidator(adminConfigValidator);
        var defaultConfigTester = new EnrichmentConfigTester(
                new SiembolJsonSchemaValidator(EnrichmentTestingSpecificationDto.class),
                testSchemaUi.get(),
                compiler);
        context.setConfigTesters(List.of(defaultConfigTester));
        LOG.info("Initialising enrichment config schema service completed");
        return new EnrichmentSchemaService(compiler, context);
    }
}