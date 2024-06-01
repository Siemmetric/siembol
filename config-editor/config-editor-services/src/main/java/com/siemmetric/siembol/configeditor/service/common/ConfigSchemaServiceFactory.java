package com.siemmetric.siembol.configeditor.service.common;

import com.siemmetric.siembol.configeditor.common.ConfigSchemaService;
import com.siemmetric.siembol.configeditor.model.AdditionalConfigTesters;
import com.siemmetric.siembol.configeditor.model.ConfigEditorUiLayout;

import java.util.Map;
import java.util.Optional;

public interface ConfigSchemaServiceFactory {
    ConfigSchemaService createConfigSchemaService(
            ConfigEditorUiLayout uiLayout,
            Optional<Map<String, String>> attributes,
            Optional<AdditionalConfigTesters> additionalTesters) throws Exception;
}
