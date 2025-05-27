package com.fx.exchange.api;

import com.fx.exchange.controller.ConvertApi;
import com.fx.exchange.controller.ConvertApiDelegate;
import com.fx.exchange.model.ConversionRequestDto;
import com.fx.exchange.model.ConversionResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component("convertApiDelegate")
public class ConvertApiHandler implements ConvertApiDelegate {
    @Override
    public ResponseEntity<ConversionResponseDto> convertCurrency(ConversionRequestDto conversionRequestDto) {
        return ConvertApiDelegate.super.convertCurrency(conversionRequestDto);
    }
}
