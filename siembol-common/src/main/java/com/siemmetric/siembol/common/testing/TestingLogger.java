package com.siemmetric.siembol.common.testing;

import java.util.Map;

public interface TestingLogger {

    default void appendMessage(String msg) {}

    default void appendMap(Map<String, Object> map) {}

    default String getLog() {
        return null;
    }

    default boolean isActive() {
        return false;
    }
}
