package com.siemmetric.siembol.configeditor.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRawValue;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.JsonNode;
/**
 * A data transfer object that represents a config importer request
 *
 * <p>This class represents config importer. It includes properties such as the name of the importer,
 * a config to import and importer attributes.
 *
 * @author  Marian Novotny
 * @see JsonProperty
 * @see JsonRawValue
 */
public class ImportConfigRequestDto {
    @JsonProperty("importer_name")
    private String importerName;
    @JsonProperty("importer_attributes")
    @JsonRawValue
    private String importerAttributes;
    @JsonProperty("config_to_import")
    private String configToImport;

    public String getImporterAttributes() {
        return importerAttributes;
    }

    @JsonSetter("importer_attributes")
    public void setImporterAttributes(JsonNode node) {
        this.importerAttributes = node.toString();
    }

    @JsonIgnore
    public void setImporterAttributes(String importerAttributes) {
        this.importerAttributes = importerAttributes;
    }

    public String getConfigToImport() {
        return configToImport;
    }

    public void setConfigToImport(String configToImport) {
        this.configToImport = configToImport;
    }

    public String getImporterName() {
        return importerName;
    }

    public void setImporterName(String importerName) {
        this.importerName = importerName;
    }
}
