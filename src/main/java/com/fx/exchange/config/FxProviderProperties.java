package com.fx.exchange.config;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Getter
@ConfigurationProperties(prefix = "fx.provider")
public class FxProviderProperties {
    private String url;
    private String apiKey;
    private String baseCurrency;

}
