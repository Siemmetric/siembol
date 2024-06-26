package com.siemmetric.siembol.configeditor.rest.authorisation;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import com.siemmetric.siembol.common.authorisation.SiembolUnauthenticatedSecurityAdapter;

@ConditionalOnProperty(prefix = "config-editor-auth", value = "type", havingValue = "disabled")
@Configuration
@EnableWebSecurity
public class UnauthenticatedSecurityAdapter extends SiembolUnauthenticatedSecurityAdapter {
}
