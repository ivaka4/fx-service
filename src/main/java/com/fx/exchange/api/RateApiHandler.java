package com.fx.exchange.api;

import com.fx.exchange.controller.RateApi;
import com.fx.exchange.controller.RateApiDelegate;
import com.fx.exchange.model.ExchangeRateDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component("rateApiDelegate")
public class RateApiHandler implements RateApiDelegate {

    @Override
    public ResponseEntity<ExchangeRateDto> getRate(String from, String to) {
        return RateApiDelegate.super.getRate(from, to);
    }
}
