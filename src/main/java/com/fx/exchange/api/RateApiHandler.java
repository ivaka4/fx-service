package com.fx.exchange.api;

import com.fx.exchange.controller.RateApi;
import com.fx.exchange.model.ExchangeRateResponse;
import com.fx.exchange.service.ConversionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequiredArgsConstructor
@Slf4j
public class RateApiHandler implements RateApi {
    private final ConversionService conversionService;

    @Override
    public ResponseEntity<ExchangeRateResponse> getRate(String from, String to) {
        log.info("[Get rate request came for {} currency to {}]", from, to);
        BigDecimal rate = conversionService.getRate(from, to);

        return ResponseEntity.ok(new ExchangeRateResponse().from(from).to(to).rate(rate));
    }
}
