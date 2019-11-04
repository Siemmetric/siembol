package uk.co.gresearch.nortem.parsers.transformations;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class TransformationsLibrary {
    public static Map<String, Object> fieldTransformation(Map<String, Object> log,
                                                          Function<String, String> fun) {
        List<Pair<String, String>> changed = new ArrayList<>();
        for (String field : log.keySet()) {
            String changedField = fun.apply(field);
            if (!field.equals(changedField)) {
                changed.add(Pair.of(field, changedField));
            }
        }

        for (Pair<String, String> replace : changed) {
            Object value = log.get(replace.getLeft());
            log.remove(replace.getLeft());
            log.put(replace.getRight(), value);
        }

        return log;
    }

    public static Map<String, Object> valueTransformation(Map<String, Object> log,
                                                          Function<Object, Object> fun,
                                                          FieldFilter filter) {
        for (String field : log.keySet()) {
            if (!filter.match(field)) {
                continue;
            }

            log.put(field, fun.apply(log.get(field)));
        }

        return log;
    }

    public static Object trim(Object obj) {
        if (!(obj instanceof String)) {
            return obj;
        }

        return ((String) obj).trim();
    }

    public static Object chomp(Object obj) {
        if (!(obj instanceof String)) {
            return obj;
        }

        return StringUtils.chomp((String) obj);
    }

    public static Map<String, Object> removeFields(Map<String, Object> log, PatternFilter filter) {
        log.keySet().removeIf(x -> filter.match(x));
        return log;
    }

    public static Map<String, Object> transform(List<Transformation> transformations, Map<String, Object> map) {
        Map<String, Object> current = map;
        for (Transformation transformation : transformations) {
            current = transformation.apply(current);
        }
        return current;
    }
}