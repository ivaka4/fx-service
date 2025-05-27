package com.fx.exchange.api;

import com.fx.exchange.controller.RateApi;
import com.fx.exchange.model.ExchangeRateResponse;
import com.fx.exchange.service.ExchangeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class RateApiHandler implements RateApi {
    private final ExchangeService exchangeService;

    @Override
    public ResponseEntity<ExchangeRateResponse> getRate(String from, String to) {
        log.info("[Get rate request came for {} currency to {}]", from, to);
        ExchangeRateResponse dto = exchangeService.getCurrentRate(from, to);
        return ResponseEntity.ok(dto);
    }
}
