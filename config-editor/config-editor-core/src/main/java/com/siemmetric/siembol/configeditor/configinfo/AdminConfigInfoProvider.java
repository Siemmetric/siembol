package com.siemmetric.siembol.configeditor.configinfo;

import com.siemmetric.siembol.configeditor.common.ConfigInfo;
import com.siemmetric.siembol.configeditor.common.ConfigInfoType;
import com.siemmetric.siembol.configeditor.common.UserInfo;
import com.siemmetric.siembol.configeditor.model.ConfigEditorFile;

import java.util.List;
/**
 * An object for providing metadata about a json admin configuration change
 *
 * <p>This class implements ConfigInfoProvider interface. It provides metadata about a json admin configuration change.
 * It provides information such as the author of the change, type of change, and the version of the admin configuration.
 *
 * @author  Marian Novotny
 * @see ConfigInfoProvider
 *
 */
public class AdminConfigInfoProvider implements ConfigInfoProvider {
    private static final String UNSUPPORTED_MESSAGE = "Not supported operation";
    private static final String ADMIN_CONFIG_FILE_NAME = "admin_config.json";
    private static final String VERSION_FIELD = "config_version";
    private static final String UNDEFINED = "undefined";
    private final JsonConfigInfoProvider jsonHelperProvider;

    public AdminConfigInfoProvider() {
        jsonHelperProvider = new JsonConfigInfoProvider.Builder()
                .configAuthorField(UNDEFINED)
                .configNameField(UNDEFINED)
                .configNamePrefixField(UNDEFINED)
                .configsVersionField(VERSION_FIELD)
                .configVersionField(UNDEFINED)
                .setConfigInfoType(ConfigInfoType.ADMIN_CONFIG)
                .releaseFilename(ADMIN_CONFIG_FILE_NAME)
                .build();
    }

    @Override
    public ConfigInfo getConfigInfo(UserInfo user, String config) {
        throw new UnsupportedOperationException(UNSUPPORTED_MESSAGE);
    }

    @Override
    public ConfigInfo getReleaseInfo(UserInfo user, String release) {
        return jsonHelperProvider.getReleaseInfo(user, release);
    }

    @Override
    public int getReleaseVersion(List<ConfigEditorFile> files) {
        return jsonHelperProvider.getReleaseVersion(files);
    }

    @Override
    public int getReleaseVersion(String content) {
        return jsonHelperProvider.getReleaseVersion(content);
    }

    @Override
    public boolean isConfigInRelease(String release, String configName) {
        throw new UnsupportedOperationException(UNSUPPORTED_MESSAGE);
    }

    @Override
    public boolean isReleaseFile(String filename) {
        return jsonHelperProvider.isReleaseFile(filename);
    }

    @Override
    public ConfigInfoType getConfigInfoType() {
        return jsonHelperProvider.getConfigInfoType();
    }

    @Override
    public ConfigEditorFile.ContentType getFileContentType() {
        return ConfigEditorFile.ContentType.RAW_JSON_STRING;
    }

    @Override
    public boolean isStoreFile(String filename) {
        throw new UnsupportedOperationException(UNSUPPORTED_MESSAGE);
    }
}
