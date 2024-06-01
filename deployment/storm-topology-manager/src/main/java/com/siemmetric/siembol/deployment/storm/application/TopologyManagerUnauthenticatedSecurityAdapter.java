package com.siemmetric.siembol.deployment.storm.application;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import com.siemmetric.siembol.common.authorisation.SiembolUnauthenticatedSecurityAdapter;

@Configuration
@EnableWebSecurity
public class TopologyManagerUnauthenticatedSecurityAdapter extends SiembolUnauthenticatedSecurityAdapter {
}
