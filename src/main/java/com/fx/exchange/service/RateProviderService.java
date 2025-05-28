package com.fx.exchange.service;

import com.fx.exchange.config.FxProviderProperties;
import com.fx.exchange.exception.ExternalServiceException;
import com.fx.exchange.exception.RateNotFoundException;
import com.fx.exchange.model.CurrencyLayerResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.math.MathContext;
import java.net.URI;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class RateProviderService {
    private final RestTemplate restTemplate;
    private final FxProviderProperties fxProviderProperties;

    /**
     * Retrieves the FX rate from `sourceCurrency` to `targetCurrency`.
     *
     * @throws IllegalArgumentException if codes are blank
     * @throws ExternalServiceException on HTTP or provider error
     * @throws RateNotFoundException    if the expected quote is missing
     */
    public BigDecimal getRate(String sourceCurrency, String targetCurrency) {

        String baseCurrency = fxProviderProperties.getBaseCurrency();
        URI requestUri = UriComponentsBuilder
                .fromHttpUrl(fxProviderProperties.getUrl())
                .queryParam("access_key", fxProviderProperties.getApiKey())
                .queryParam("source", baseCurrency)
                .queryParam("currencies", String.join(",", sourceCurrency, targetCurrency))
                .queryParam("format", 1)
                .build()
                .toUri();

        CurrencyLayerResponse response;
        try {
            response = restTemplate.getForObject(requestUri, CurrencyLayerResponse.class);
        } catch (RestClientException ex) {
            log.error("Failed while calling the currency layer api", ex);
            throw new ExternalServiceException(
                    "Failed to call FX provider at " + requestUri + ": " + ex.getMessage(), ex
            );
        }

        if (response == null) {
            log.error("Currency layer api returned empty response");
            throw new ExternalServiceException("FX provider returned empty response");
        }
        if (!response.getSuccess()) {
            log.error("Currency layer api returned error: {}", response.getError());
            throw new ExternalServiceException("FX provider error: " + response.getError().getInfo());
        }

        Map<String, BigDecimal> quotes = response.getQuotes();
        BigDecimal quoteSource = quotes.get(baseCurrency + sourceCurrency);
        BigDecimal quoteTarget = quotes.get(baseCurrency + targetCurrency);

        if (sourceCurrency.equals(baseCurrency)) {
            return requireRate(quoteTarget, "Missing rate for " + baseCurrency + targetCurrency);
        }
        if (targetCurrency.equals(baseCurrency)) {
            return BigDecimal.ONE.divide(
                    requireRate(quoteSource, "Missing rate for " + baseCurrency + sourceCurrency),
                    MathContext.DECIMAL128
            );
        }

        BigDecimal numerator = requireRate(quoteTarget, "Missing rate for " + baseCurrency + targetCurrency);
        BigDecimal denominator = requireRate(quoteSource, "Missing rate for " + baseCurrency + sourceCurrency);
        return numerator.divide(denominator, MathContext.DECIMAL128);
    }

    private BigDecimal requireRate(BigDecimal rate, String msg) {
        if (rate == null) throw new RateNotFoundException(msg);
        return rate;
    }
}
