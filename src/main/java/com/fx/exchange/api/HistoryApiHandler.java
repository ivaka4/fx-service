package com.fx.exchange.api;

import com.fx.exchange.controller.HistoryApi;
import com.fx.exchange.model.ConversionResponse;
import com.fx.exchange.service.ConversionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class HistoryApiHandler implements HistoryApi {
    private final ConversionService conversionService;

    @Override
    public ResponseEntity<List<ConversionResponse>> getConversionHistory(UUID transactionId, OffsetDateTime startDate, OffsetDateTime endDate, Integer page, Integer size) {

        List<ConversionResponse> history = conversionService.history(transactionId, startDate, endDate, page, size);
        return ResponseEntity.ok(history);
    }
}
