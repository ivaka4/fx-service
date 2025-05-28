package com.fx.exchange.api;

import com.fx.exchange.controller.ConvertApi;

import com.fx.exchange.model.ConversionRequest;
import com.fx.exchange.model.ConversionResponse;
import com.fx.exchange.service.ConversionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ConvertApiHandler implements ConvertApi {
    private final ConversionService conversionService;

    @Override
    public ResponseEntity<ConversionResponse> convertCurrency(ConversionRequest body) {
        ConversionResponse response = conversionService.convert(body);
        return ResponseEntity.ok(response);
    }
}
