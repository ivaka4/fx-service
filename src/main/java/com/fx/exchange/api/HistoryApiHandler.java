package com.fx.exchange.api;

import com.fx.exchange.controller.HistoryApi;
import com.fx.exchange.model.ConversionResponse;
import com.fx.exchange.service.ExchangeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class HistoryApiHandler implements HistoryApi {
    private final ExchangeService exchangeService;

    @Override
    public ResponseEntity<List<ConversionResponse>> getConversionHistory(UUID transactionId, LocalDate date, Integer page, Integer size) {

        List<ConversionResponse> history = exchangeService.getHistory(transactionId, date, page, size);
        return ResponseEntity.ok(history);
    }
}
