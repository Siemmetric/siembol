package com.siemmetric.siembol.response.stream.ruleservice;

import com.siemmetric.siembol.response.engine.ResponseEngine;

public interface RulesProvider {
    ResponseEngine getEngine();

    default boolean isInitialised() {
        return getEngine() != null;
    }
}
