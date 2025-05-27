package com.fx.exchange.api;

import com.fx.exchange.controller.HistoryApi;
import com.fx.exchange.controller.HistoryApiDelegate;
import com.fx.exchange.model.ConversionResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component("historyApiDelegate")
public class HistoryApiHandler implements HistoryApiDelegate {
    @Override
    public ResponseEntity<List<ConversionResponseDto>> getConversionHistory(String transactionId, LocalDate date, Integer page, Integer size) {
        return HistoryApiDelegate.super.getConversionHistory(transactionId, date, page, size);
    }
}
