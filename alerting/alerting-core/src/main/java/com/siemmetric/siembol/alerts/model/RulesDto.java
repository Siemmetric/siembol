package com.siemmetric.siembol.alerts.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.reinert.jjschema.Attributes;
import com.siemmetric.siembol.alerts.common.AlertingTags;

import java.util.Arrays;
import java.util.List;
/**
 * A data transfer object for representing alerting rules
 *
 * <p>This class is used for json (de)serialisation of alerting rules and
 * for generating json schema from this class using annotations.
 *
 * @author  Marian Novotny
 * @see com.github.reinert.jjschema.Attributes
 * @see com.fasterxml.jackson.annotation.JsonProperty
 */
@Attributes(title = "rules", description = "Rules for real-time alert matching")
public class RulesDto {
    public RulesDto() {
        TagDto tag = new TagDto();
        tag.setTagName(AlertingTags.DETECTION_SOURCE_TAG_NAME.toString());
        tag.setTagValue(AlertingTags.DETECTION_SOURCE_TAG_VALUE.toString());
        tags = Arrays.asList(tag);
    }

    @JsonProperty("rules_version")
    @Attributes(required = true, description = "The version of the rules", minimum = 0)
    Integer rulesVersion;

    @JsonProperty("tags")
    @Attributes(required = true, description = "The tags that will be added to the alert")
    private List<TagDto> tags;
    @JsonProperty("rules_protection")
    @Attributes(description = "Global protection specification for rules")
    RuleProtectionDto rulesProtection = new RuleProtectionDto();

    @JsonProperty("rules")
    @Attributes(required = true, description = "Rules of the release", minItems = 1)
    List<RuleDto> rules;

    public Integer getRulesVersion() {
        return rulesVersion;
    }

    public void setRulesVersion(Integer rulesVersion) {
        this.rulesVersion = rulesVersion;
    }

    public RuleProtectionDto getRulesProtection() {
        return rulesProtection;
    }

    public void setRulesProtection(RuleProtectionDto rulesProtection) {
        this.rulesProtection = rulesProtection;
    }

    public List<TagDto> getTags() {
        return tags;
    }

    public void setTags(List<TagDto> tags) {
        this.tags = tags;
    }

    public List<RuleDto> getRules() {
        return rules;
    }

    public void setRules(List<RuleDto> rules) {
        this.rules = rules;
    }
}
