package com.siemmetric.siembol.configeditor.service.enrichments;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.internal.verification.VerificationModeFactory;
import com.siemmetric.siembol.common.jsonschema.SiembolJsonSchemaValidator;
import com.siemmetric.siembol.common.result.SiembolAttributes;
import com.siemmetric.siembol.common.result.SiembolResult;
import com.siemmetric.siembol.configeditor.common.ConfigSchemaService;
import com.siemmetric.siembol.configeditor.model.ConfigEditorResult;
import com.siemmetric.siembol.configeditor.model.ConfigEditorUiLayout;
import com.siemmetric.siembol.configeditor.service.common.ConfigSchemaServiceContext;
import com.siemmetric.siembol.enrichments.common.EnrichmentAttributes;
import com.siemmetric.siembol.enrichments.common.EnrichmentResult;
import com.siemmetric.siembol.enrichments.compiler.EnrichmentCompiler;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static com.siemmetric.siembol.enrichments.common.EnrichmentResult.StatusCode.ERROR;
import static com.siemmetric.siembol.enrichments.common.EnrichmentResult.StatusCode.OK;

public class EnrichmentSchemaServiceTest {
    private EnrichmentSchemaService enrichmentsSchemaService;
    private final String schema = "dummmy schema";
    private final String adminSchema = "dummmy admin schema";
    private final String testConfig = "dummmy enrichments config";
    private final String testSpecification = "dummmy test specification";
    private final String testConfigs = "dummmy enrichments configs";
    private final String testResult = "dummy test result";
    private final String testRawResult = "dummy test raw result";

    private EnrichmentCompiler compiler;

    private EnrichmentAttributes enrichmentAttributes;
    private EnrichmentResult enrichmentResult;
    private ConfigSchemaServiceContext context;
    private SiembolJsonSchemaValidator adminConfigValidator;
    private SiembolResult validationResult;

    @Before
    public void setUp() {
        compiler = Mockito.mock(EnrichmentCompiler.class);
        adminConfigValidator = Mockito.mock(SiembolJsonSchemaValidator.class);
        validationResult = new SiembolResult(SiembolResult.StatusCode.OK, new SiembolAttributes());
        when(adminConfigValidator.validate(eq(testConfig))).thenReturn(validationResult);

        context = new ConfigSchemaServiceContext();
        context.setConfigSchema(schema);
        context.setTestSchema(schema);
        context.setAdminConfigSchema(adminSchema);
        context.setAdminConfigValidator(adminConfigValidator);

        this.enrichmentsSchemaService = new EnrichmentSchemaService(compiler, context);
        enrichmentAttributes = new EnrichmentAttributes();
        enrichmentResult = new EnrichmentResult(OK, enrichmentAttributes);
        Mockito.when(compiler.compile(anyString())).thenReturn(enrichmentResult);

        Mockito.when(compiler.validateConfiguration(anyString())).thenReturn(enrichmentResult);
        Mockito.when(compiler.validateConfigurations(anyString())).thenReturn(enrichmentResult);
        Mockito.when(compiler.testConfiguration(anyString(), anyString())).thenReturn(enrichmentResult);
        Mockito.when(compiler.testConfigurations(anyString(), anyString())).thenReturn(enrichmentResult);
    }

    @Test
    public void getSchemaOk() {
        ConfigEditorResult ret = enrichmentsSchemaService.getSchema();
        Assert.assertEquals(ConfigEditorResult.StatusCode.OK, ret.getStatusCode());
        Assert.assertEquals(schema, ret.getAttributes().getRulesSchema());
    }

    /**
    @Test
    public void getTestSchemaOK() {
        ConfigEditorResult ret = enrichmentsSchemaService.getTestSchema();
        Assert.assertEquals(ConfigEditorResult.StatusCode.OK, ret.getStatusCode());
        Assert.assertNotNull(ret.getAttributes().getTestSchema());
    }
     */

    @Test
    public void validateConfigurationsOK() {
        ConfigEditorResult ret = enrichmentsSchemaService.validateConfigurations(testConfigs);
        Mockito.verify(compiler, times(1)).validateConfigurations(testConfigs);
        Assert.assertEquals(ConfigEditorResult.StatusCode.OK, ret.getStatusCode());
    }

    @Test
    public void validateConfigurationOK() {
        ConfigEditorResult ret = enrichmentsSchemaService.validateConfiguration(testConfig);
        Mockito.verify(compiler, times(1)).validateConfiguration(testConfig);
        Assert.assertEquals(ConfigEditorResult.StatusCode.OK, ret.getStatusCode());
    }

    @Test
    public void validateConfigurationsError() {
        enrichmentAttributes.setMessage("error");
        enrichmentResult = new EnrichmentResult(ERROR, enrichmentAttributes);
        Mockito.when(compiler.validateConfigurations(anyString())).thenReturn(enrichmentResult);
        ConfigEditorResult ret = enrichmentsSchemaService.validateConfigurations(testConfigs);
        Mockito.verify(compiler, times(1)).validateConfigurations(testConfigs);
        Assert.assertEquals(ConfigEditorResult.StatusCode.BAD_REQUEST, ret.getStatusCode());
        Assert.assertEquals("error", ret.getAttributes().getMessage());
    }

    @Test
    public void validateConfigurationError() {
        enrichmentAttributes.setMessage("error");
        enrichmentResult = new EnrichmentResult(ERROR, enrichmentAttributes);
        Mockito.when(compiler.validateConfiguration(anyString())).thenReturn(enrichmentResult);
        ConfigEditorResult ret = enrichmentsSchemaService.validateConfiguration(testConfigs);
        Mockito.verify(compiler, times(1)).validateConfiguration(testConfigs);
        Assert.assertEquals(ConfigEditorResult.StatusCode.BAD_REQUEST, ret.getStatusCode());
        Assert.assertEquals("error", ret.getAttributes().getMessage());
    }

    @Test
    public void createEnrichmentSchemaServiceEmptyUiConfig() throws Exception {
        ConfigSchemaService service = EnrichmentSchemaService
                .createEnrichmentsSchemaService(new ConfigEditorUiLayout());
        Assert.assertNotNull(service);
    }

    @Test
    public void getAdminConfigSchemaOK() {
        ConfigEditorResult ret = enrichmentsSchemaService.getAdminConfigurationSchema();
        Assert.assertEquals(ConfigEditorResult.StatusCode.OK, ret.getStatusCode());
        Assert.assertEquals(adminSchema, ret.getAttributes().getAdminConfigSchema());
    }

    @Test
    public void validateAdminConfigOK() {
        ConfigEditorResult ret = enrichmentsSchemaService.validateAdminConfiguration(testConfig);
        Assert.assertEquals(ConfigEditorResult.StatusCode.OK, ret.getStatusCode());
        verify(adminConfigValidator, VerificationModeFactory.times(1)).validate(testConfig);
    }

    @Test
    public void validateAdminConfigInvalid() {
        when(adminConfigValidator.validate(eq(testConfig)))
                .thenReturn(new SiembolResult(SiembolResult.StatusCode.ERROR, new SiembolAttributes()));
        ConfigEditorResult ret = enrichmentsSchemaService.validateAdminConfiguration(testConfig);
        Assert.assertEquals(ConfigEditorResult.StatusCode.BAD_REQUEST, ret.getStatusCode());
        verify(adminConfigValidator, VerificationModeFactory.times(1)).validate(testConfig);
    }

    @Test
    public void getImportersEmpty() {
        ConfigEditorResult ret = enrichmentsSchemaService.getImporters();
        Assert.assertEquals(ConfigEditorResult.StatusCode.OK, ret.getStatusCode());
        Assert.assertNotNull(ret.getAttributes().getConfigImporters());
        Assert.assertTrue(ret.getAttributes().getConfigImporters().isEmpty());
    }
}
