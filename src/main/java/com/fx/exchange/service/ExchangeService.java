package com.fx.exchange.service;

import com.fx.exchange.config.FxProviderProperties;
import com.fx.exchange.mapper.ConversionMapper;
import com.fx.exchange.model.ConversionRequest;
import com.fx.exchange.model.ConversionResponse;
import com.fx.exchange.model.CurrencyLayerResponse;
import com.fx.exchange.model.ExchangeRateResponse;
import com.fx.exchange.persistance.entity.ConversionEntity;
import com.fx.exchange.persistance.repository.ConversionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.math.MathContext;
import java.net.URI;
import java.time.Duration;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ExchangeService {

    private final RestTemplate restTemplate;
    private final FxProviderProperties props;
    private final RedisTemplate<String, BigDecimal> redis;
    private final ConversionRepository repo;
    private final ConversionMapper conversionMapper;


    public ExchangeRateResponse getCurrentRate(String from, String to) {
        String key = from + ":" + to;
        BigDecimal cached = null;
        try {
            cached = redis.opsForValue().get(key);
        } catch (RedisConnectionFailureException e) {
            log.warn("No record in redis, will take from external api");
        }

        if (cached != null) {
            return new ExchangeRateResponse().from(from).to(to).rate(cached);
        }
        ExchangeRateResponse dto = fetchRateFromProvider(from, to);
        redis.opsForValue().set(key, dto.getRate(), Duration.ofMinutes(5));
        return dto;
    }


    public ConversionResponse convert(ConversionRequest req) {
        BigDecimal rate = getCurrentRate(req.getFrom(), req.getTo()).getRate();
        BigDecimal converted = req.getAmount().multiply(rate, MathContext.DECIMAL128);

        ConversionEntity entity = conversionMapper.toConversionEntity(req);
        repo.save(entity);

        return toConversionResponse(entity);
    }

    public List<ConversionResponse> getHistory(UUID transactionId,
                                               LocalDate date,
                                               Integer page,
                                               Integer size) {
        if (transactionId != null) {
            return repo.findByTransactionId(transactionId)
                    .map(this::toConversionResponse)
                    .map(List::of)
                    .orElse(List.of());
        }
        var pageable = PageRequest.of(page, size);
        return repo.findByConvertedAtBetween(date, date, pageable)
                .stream()
                .map(this::toConversionResponse)
                .collect(Collectors.toList());
    }

    private ExchangeRateResponse fetchRateFromProvider(String from, String to) {
        String base = props.getBaseCurrency();
        String currencies = String.join(",", from, to);
        // Build URI with UriComponentsBuilder
        URI uri = UriComponentsBuilder
                .fromHttpUrl(props.getUrl())
                .queryParam("access_key", props.getApiKey())
                .queryParam("currencies", currencies)
                .queryParam("source", base)
                .queryParam("format", 1)
                .build()
                .toUri();

        CurrencyLayerResponse resp = restTemplate.getForObject(uri, CurrencyLayerResponse.class);
        if (resp == null || !resp.getSuccess()) {
            throw new IllegalStateException("Failed to fetch FX rates from provider");
        }

        String keyTo = base + to;
        BigDecimal quoteTo = resp.getQuotes().get(keyTo);
        if (quoteTo == null) {
            throw new IllegalStateException("Missing FX rate for " + keyTo);
        }

        BigDecimal rate;
        if (from.equals(base)) {
            rate = quoteTo;
        } else {
            String keyFrom = base + from;
            BigDecimal quoteFrom = resp.getQuotes().get(keyFrom);
            if (quoteFrom == null) {
                throw new IllegalStateException("Missing FX rate for " + keyFrom);
            }
            if (to.equals(base)) {
                rate = BigDecimal.ONE.divide(quoteFrom, MathContext.DECIMAL128);
            } else {
                rate = quoteTo.divide(quoteFrom, MathContext.DECIMAL128);
            }
        }

        return new ExchangeRateResponse().from(from).to(to).rate(rate);
    }

    private ConversionResponse toConversionResponse(ConversionEntity e) {
        return conversionMapper.toConversionResponse(e);
    }
}
