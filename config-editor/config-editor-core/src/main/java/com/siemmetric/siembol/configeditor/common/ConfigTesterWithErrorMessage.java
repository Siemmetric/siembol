package com.siemmetric.siembol.configeditor.common;

import com.siemmetric.siembol.configeditor.model.ConfigEditorResult;
import com.siemmetric.siembol.configeditor.model.ErrorMessages;
import com.siemmetric.siembol.configeditor.model.ErrorResolutions;
import com.siemmetric.siembol.configeditor.model.ErrorTitles;

import java.util.EnumSet;
import java.util.function.Supplier;
/**
 * An object for configuration tester with enhanced error messages
 *
 * <p>This class implements ConfigTester interface, and it extends ServiceWithErrorMessage class.
 * It enriches error messages on error.
 *
 * @author  Marian Novotny
 * @see ServiceWithErrorMessage
 * @see ConfigTester
 */
public class ConfigTesterWithErrorMessage  extends ServiceWithErrorMessage<ConfigTester> implements ConfigTester {
    public ConfigTesterWithErrorMessage(ConfigTester service) {
        super(service);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EnumSet<ConfigTesterFlag> getFlags() {
        return service.getFlags();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ConfigEditorResult getTestSpecificationSchema() {
        return service.getTestSpecificationSchema();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ConfigEditorResult validateTestSpecification(String attributes) {
        Supplier<ConfigEditorResult> fun = () -> service.validateTestSpecification(attributes);
        return executeInternally(fun, ErrorTitles.VALIDATION_GENERIC.getTitle(),
                ErrorMessages.VALIDATION_GENERIC.getMessage(),
                ErrorResolutions.VALIDATION.getResolution());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ConfigEditorResult testConfiguration(String configuration, String testSpecification) {
        Supplier<ConfigEditorResult> fun = () -> service.testConfiguration(configuration, testSpecification);
        return executeInternally(fun, ErrorTitles.TESTING_GENERIC.getTitle(),
                ErrorMessages.TESTING_GENERIC.getMessage(),
                ErrorResolutions.GENERIC_BAD_REQUEST.getResolution());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ConfigEditorResult testConfigurations(String configurations, String testSpecification) {
        Supplier<ConfigEditorResult> fun = () -> service.testConfigurations(configurations, testSpecification);
        return executeInternally(fun, ErrorTitles.TESTING_GENERIC.getTitle(),
                ErrorMessages.VALIDATION_GENERIC.getMessage(),
                ErrorResolutions.GENERIC_BAD_REQUEST.getResolution());
    }
}
