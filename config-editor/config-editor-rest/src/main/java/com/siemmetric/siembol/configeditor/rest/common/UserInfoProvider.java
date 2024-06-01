package com.siemmetric.siembol.configeditor.rest.common;

import com.siemmetric.siembol.configeditor.common.UserInfo;

public interface UserInfoProvider {
    UserInfo getUserInfo(Object principal);
}
