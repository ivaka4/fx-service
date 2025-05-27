package com.fx.exchange.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
@ConfigurationProperties(prefix = "fx.provider")
public class FxProviderProperties {
    private String url;
    private String apiKey;
    private String baseCurrency;

}
