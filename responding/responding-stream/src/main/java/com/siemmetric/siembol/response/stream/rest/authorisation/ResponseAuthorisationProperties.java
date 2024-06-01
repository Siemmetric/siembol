package com.siemmetric.siembol.response.stream.rest.authorisation;

import org.springframework.boot.context.properties.ConfigurationProperties;
import com.siemmetric.siembol.common.authorisation.SiembolAuthorisationProperties;

@ConfigurationProperties(prefix = "siembol-response-auth")
public class ResponseAuthorisationProperties extends SiembolAuthorisationProperties {
}
