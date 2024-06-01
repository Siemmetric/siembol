package com.siemmetric.siembol.parsers.factory;

import com.siemmetric.siembol.parsers.common.SiembolParser;
import com.siemmetric.siembol.parsers.common.ParserResult;
import com.siemmetric.siembol.parsers.extractors.ParserExtractor;
import com.siemmetric.siembol.parsers.transformations.Transformation;

import java.util.List;
/**
 * An object for representing a parser factory result attributes
 *
 * <p>This class represents parser factory result attributes used in parser factory result.
 *
 * @author Marian Novotny
 * @see ParserFactoryResult
 *
 */
public class ParserFactoryAttributes {
    private List<ParserExtractor> extractors;
    private List<Transformation> transformations;
    private SiembolParser siembolParser;
    private String message;
    private ParserResult parserResult;
    private String jsonSchema;
    private String parserName;

    public List<ParserExtractor> getExtractors() {
        return extractors;
    }

    public void setExtractors(List<ParserExtractor> extractors) {
        this.extractors = extractors;
    }

    public List<Transformation> getTransformations() {
        return transformations;
    }

    public void setTransformations(List<Transformation> transformations) {
        this.transformations = transformations;
    }

    public SiembolParser getSiembolParser() {
        return siembolParser;
    }

    public void setSiembolParser(SiembolParser siembolParser) {
        this.siembolParser = siembolParser;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ParserResult getParserResult() {
        return parserResult;
    }

    public void setParserResult(ParserResult parserResult) {
        this.parserResult = parserResult;
    }

    public String getJsonSchema() {
        return jsonSchema;
    }

    public void setJsonSchema(String jsonSchema) {
        this.jsonSchema = jsonSchema;
    }

    public String getParserName() {
        return parserName;
    }

    public void setParserName(String parserName) {
        this.parserName = parserName;
    }
}
