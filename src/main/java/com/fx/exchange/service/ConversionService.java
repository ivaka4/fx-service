package com.fx.exchange.service;

import com.fx.exchange.exception.NotFoundException;
import com.fx.exchange.mapper.ConversionMapper;
import com.fx.exchange.model.ConversionRequest;
import com.fx.exchange.model.ConversionResponse;
import com.fx.exchange.persistance.entity.ConversionEntity;
import com.fx.exchange.persistance.repository.ConversionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ConversionService {

    private final RateProviderService rateProviderService;
    private final RedisRateCacheService cacheService;
    private final ConversionRepository repository;
    private final ConversionMapper mapper;

    public BigDecimal getRate(String from, String to) {
        return cacheService.lookup(from, to)
                .orElseGet(() -> {
                    BigDecimal fresh = rateProviderService.getRate(from, to);
                    cacheService.store(from, to, fresh);
                    return fresh;
                });
    }

    public ConversionResponse convert(ConversionRequest req) {
        if (req.getAmount() == null || req.getAmount().signum() <= 0) {
            throw new IllegalArgumentException("Amount must be a positive number");
        }

        String from = req.getFrom(), to = req.getTo();
        BigDecimal rate = cacheService
                .lookup(from, to)
                .orElseGet(() -> {
                    BigDecimal r = rateProviderService.getRate(from, to);
                    cacheService.store(from, to, r);
                    return r;
                });

        BigDecimal convertedAmount = req.getAmount().multiply(rate);

        ConversionEntity entity = mapper.toConversionEntity(req, rate, convertedAmount);
        repository.save(entity);

        return mapper.toConversionResponse(entity);
    }

    public List<ConversionResponse> history(UUID txId, OffsetDateTime startDate, OffsetDateTime endDate, int page, int size) {
        if (txId != null) {
            return repository.findByTransactionId(txId)
                    .map(mapper::toConversionResponse)
                    .map(List::of)
                    .orElseThrow(() ->
                            new NotFoundException("No conversion found for transactionId=" + txId));
        }
        if (startDate!= null && endDate != null){
            return mapper.toConversionResponseList(
                    repository.findByConvertedAtBetween(startDate, endDate, PageRequest.of(page, size))
            );
        }
        return mapper.toConversionResponseList(repository.findAll(PageRequest.of(page, size)).stream().toList());
    }
}
