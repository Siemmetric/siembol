package com.siemmetric.siembol.configeditor.sync.service;

import com.siemmetric.siembol.common.model.EnrichmentTableDto;
import com.siemmetric.siembol.configeditor.model.ConfigEditorResult;

public interface EnrichmentTablesProvider {
    ConfigEditorResult getEnrichmentTables(String serviceName);
    ConfigEditorResult addEnrichmentTable(String serviceName, EnrichmentTableDto enrichmentTable);
    ConfigEditorResult updateEnrichmentTable(String serviceName, EnrichmentTableDto enrichmentTable);
}
