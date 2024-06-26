package com.siemmetric.siembol.alerts.storm.model;

import com.siemmetric.siembol.alerts.common.AlertingEngineType;
import com.siemmetric.siembol.alerts.common.AlertingFields;
import com.siemmetric.siembol.alerts.common.AlertingTags;

import java.io.Serializable;
import java.util.EnumSet;
import java.util.Map;
import java.util.Optional;
/**
 * A serializable object for representing an alerting message after triggering by an alerting engine
 *
 * <p>This class implements serializable interface and is used for representing an alerting message after
 * being triggered by an alerting engine.
 *
 * @author  Marian Novotny
 *
 */
public class AlertMessage implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final String MISSING_FIELDS_MSG = "Missing siembol alerting fields in alert: %s";
    private static final String MISSING_CORRELATION_KEY = "Missing correlation key field in alert: %s";

    public enum Flags implements Serializable {
        CORRELATION_ENGINE,
        CORRELATION_ALERT,
        VISIBLE_ALERT;
        private static final long serialVersionUID = 1L;
    }

    private final String fullRuleName;
    private final String ruleName;
    private final Number maxHourMatches;
    private final Number maxDayMatches;
    private final String alertJson;
    private final EnumSet<Flags> flags;
    private final String correlationKey;

    public AlertMessage(AlertingEngineType engineType, Map<String, Object> alert, String alertJson) {
        String correlationKey = null;
        flags = EnumSet.noneOf(Flags.class);
        if (engineType.equals(AlertingEngineType.SIEMBOL_CORRELATION_ALERTS)) {
            fullRuleName = alert.get(AlertingFields.FULL_RULE_NAME.getCorrelationAlertingName()).toString();
            ruleName = alert.get(AlertingFields.RULE_NAME.getCorrelationAlertingName()).toString();
            maxHourMatches = (Number)alert.get(AlertingFields.MAX_PER_HOUR_FIELD.getCorrelationAlertingName());
            maxDayMatches = (Number)alert.get(AlertingFields.MAX_PER_DAY_FIELD.getCorrelationAlertingName());

            flags.add(Flags.CORRELATION_ENGINE);
            flags.add(Flags.VISIBLE_ALERT);
        } else {
            fullRuleName = alert.get(AlertingFields.FULL_RULE_NAME.getAlertingName()).toString();
            ruleName = alert.get(AlertingFields.RULE_NAME.getAlertingName()).toString();
            maxHourMatches = (Number)alert.get(AlertingFields.MAX_PER_HOUR_FIELD.getAlertingName());
            maxDayMatches = (Number)alert.get(AlertingFields.MAX_PER_DAY_FIELD.getAlertingName());

            if (alert.containsKey(AlertingTags.CORRELATION_KEY_TAG_NAME.toString())) {
                flags.add(Flags.CORRELATION_ALERT);

                Object correlationVisibleTag = alert.get(AlertingTags.CORRELATION_ALERT_VISIBLE_TAG_NAME.toString());
                if (correlationVisibleTag instanceof String
                        && correlationVisibleTag.toString().equalsIgnoreCase(AlertingTags.TAG_TRUE_VALUE.toString())) {
                    this.flags.add(Flags.VISIBLE_ALERT);
                }

                if (!(alert.get(AlertingTags.CORRELATION_KEY_TAG_NAME.toString()) instanceof String)) {
                    throw new IllegalArgumentException(String.format(MISSING_CORRELATION_KEY, alert.toString()));
                }
                correlationKey = (String)alert.get(AlertingTags.CORRELATION_KEY_TAG_NAME.toString());
            } else {
                flags.add(Flags.VISIBLE_ALERT);
            }
        }

        this.alertJson = alertJson;
        if (fullRuleName == null
                || maxHourMatches == null
                || maxDayMatches == null
                || alertJson == null) {
            throw new IllegalArgumentException(String.format(MISSING_FIELDS_MSG, alert.toString()));
        }
        this.correlationKey = correlationKey;
    }

    public String getFullRuleName() {
        return fullRuleName;
    }

    public Number getMaxHourMatches() {
        return maxHourMatches;
    }

    public Number getMaxDayMatches() {
        return maxDayMatches;
    }

    public String getAlertJson() {
        return alertJson;
    }

    public boolean isCorrelationAlert() {
        return flags.contains(Flags.CORRELATION_ALERT);
    }

    public boolean isVisibleAlert() {
        return flags.contains(Flags.VISIBLE_ALERT);
    }

    public Optional<String> getCorrelationKey() {
        return Optional.ofNullable(correlationKey);
    }

    public String getRuleName() {
        return ruleName;
    }
}
