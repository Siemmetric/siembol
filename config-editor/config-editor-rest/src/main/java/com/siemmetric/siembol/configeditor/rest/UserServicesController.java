package com.siemmetric.siembol.configeditor.rest;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import com.siemmetric.siembol.configeditor.common.UserInfo;
import com.siemmetric.siembol.configeditor.model.ConfigEditorAttributes;
import com.siemmetric.siembol.configeditor.model.ConfigEditorResult;
import com.siemmetric.siembol.configeditor.rest.common.UserInfoProvider;
import com.siemmetric.siembol.configeditor.serviceaggregator.ServiceAggregator;

import static com.siemmetric.siembol.common.authorisation.SiembolAuthorisationProperties.SWAGGER_AUTH_SCHEMA;

@RestController
@SecurityRequirement(name = SWAGGER_AUTH_SCHEMA)
public class UserServicesController {
    @Autowired
    private ServiceAggregator serviceAggregator;
    @Autowired
    private UserInfoProvider userInfoProvider;

    @CrossOrigin
    @RequestMapping(value = "/user", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ConfigEditorAttributes> getLoggedInUser(@AuthenticationPrincipal Object principal) {
        UserInfo user = userInfoProvider.getUserInfo(principal);
        ConfigEditorAttributes attr = new ConfigEditorAttributes();
        attr.setUserName(user.getUserName());
        attr.setServices(serviceAggregator.getConfigEditorServices(user));
        return new ConfigEditorResult(ConfigEditorResult.StatusCode.OK, attr).toResponseEntity();
    }
}
