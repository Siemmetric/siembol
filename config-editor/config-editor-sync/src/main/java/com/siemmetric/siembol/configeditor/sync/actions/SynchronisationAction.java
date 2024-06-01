package com.siemmetric.siembol.configeditor.sync.actions;

import com.siemmetric.siembol.configeditor.model.ConfigEditorResult;
import com.siemmetric.siembol.configeditor.model.ConfigEditorServiceContext;

public interface SynchronisationAction {
    ConfigEditorResult execute(ConfigEditorServiceContext context);
}
