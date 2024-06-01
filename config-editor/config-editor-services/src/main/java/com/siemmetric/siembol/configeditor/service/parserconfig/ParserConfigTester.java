package com.siemmetric.siembol.configeditor.service.parserconfig;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.commons.lang3.exception.ExceptionUtils;
import com.siemmetric.siembol.common.jsonschema.SiembolJsonSchemaValidator;
import com.siemmetric.siembol.common.model.testing.ParserConfingTestSpecificationDto;
import com.siemmetric.siembol.configeditor.common.ConfigTesterBase;
import com.siemmetric.siembol.configeditor.common.ConfigTesterFlag;
import com.siemmetric.siembol.configeditor.model.ConfigEditorAttributes;
import com.siemmetric.siembol.configeditor.model.ConfigEditorResult;
import com.siemmetric.siembol.parsers.common.ParserResult;
import com.siemmetric.siembol.parsers.factory.ParserFactory;
import com.siemmetric.siembol.parsers.factory.ParserFactoryResult;

import java.util.EnumSet;
import java.util.List;
import java.util.Map;

import static com.siemmetric.siembol.configeditor.model.ConfigEditorResult.StatusCode.BAD_REQUEST;
import static com.siemmetric.siembol.configeditor.model.ConfigEditorResult.StatusCode.OK;

public class ParserConfigTester extends ConfigTesterBase<ParserFactory> {
    private static final ObjectReader TEST_LOG_READER = new ObjectMapper()
            .readerFor(ParserConfingTestSpecificationDto.class);

    private static final ObjectWriter JSON_WRITER_MESSAGES = new ObjectMapper()
            .writerFor(new TypeReference<List<Map<String, Object>>>() { })
            .with(SerializationFeature.INDENT_OUTPUT);
    private static final ObjectWriter JSON_WRITER_RESULT = new ObjectMapper()
            .setSerializationInclusion(JsonInclude.Include.NON_NULL)
            .writerFor(new TypeReference<ParserResult>() { })
            .with(SerializationFeature.INDENT_OUTPUT);

    public ParserConfigTester(SiembolJsonSchemaValidator testValidator, 
                              String testSchema, 
                              ParserFactory compiler) {
        super(testValidator, testSchema, compiler);
    }

    @Override
    public ConfigEditorResult testConfiguration(String configuration, String testSpecification) {
        var validationResult  = this.validateTestSpecification(testSpecification);
        if (validationResult.getStatusCode() != OK) {
            return ConfigEditorResult.fromMessage(BAD_REQUEST, validationResult.getAttributes().getMessage());
        }

        try {
            ParserConfingTestSpecificationDto test = TEST_LOG_READER.readValue(testSpecification);
            ParserFactoryResult parserResult = testProvider.test(configuration,
                    test.getMetadata(),
                    test.getEncoding().decode(test.getLog()));
            return fromParserFactoryTestResult(parserResult);
        } catch (Exception e) {
            return ConfigEditorResult.fromException(e);
        }
    }

    private ConfigEditorResult fromParserFactoryTestResult(ParserFactoryResult parserFactoryResult) {
        ConfigEditorAttributes attr = new ConfigEditorAttributes();
        if (parserFactoryResult.getStatusCode() != ParserFactoryResult.StatusCode.OK
                || parserFactoryResult.getAttributes().getParserResult() == null) {
            attr.setMessage(parserFactoryResult.getAttributes().getMessage());
            return new ConfigEditorResult(BAD_REQUEST, attr);
        }

        ParserResult result = parserFactoryResult.getAttributes().getParserResult();
        try {
            String testOutput = result.getException() == null
                    ? JSON_WRITER_MESSAGES.writeValueAsString(result.getParsedMessages())
                    : ExceptionUtils.getStackTrace(result.getException());
            attr.setTestResultOutput(testOutput);
            String rawTestResult = JSON_WRITER_RESULT.writeValueAsString(result);
            attr.setTestResultRawOutput(rawTestResult);
        } catch (JsonProcessingException e) {
            return ConfigEditorResult.fromException(e);
        }

        return new ConfigEditorResult(OK, attr);
    }

    @Override
    public EnumSet<ConfigTesterFlag> getFlags() {
        return EnumSet.of(ConfigTesterFlag.CONFIG_TESTING,
                ConfigTesterFlag.TEST_CASE_TESTING);
    }
}
