package com.srscons.shortlink.auth;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "google")
@Data
public class GoogleProperties {

    private String appLoginSecretPath;
    private String scope;
    private String userInfoUri;

}
