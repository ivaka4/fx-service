package com.fx.exchange.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisRateCacheService {

    private final RedisTemplate<String, BigDecimal> redis;
    private static final Duration TTL = Duration.ofMinutes(5);

    public Optional<BigDecimal> lookup(String from, String to) {
        if (from == null || to == null) {
            return Optional.empty();
        }
        String key = cacheKey(from, to);
        try {
            return Optional.ofNullable(redis.opsForValue().get(key));
        } catch (RedisConnectionFailureException ex) {
            log.warn("Redis lookup failed for key={} – proceeding without cache", key, ex);
            return Optional.empty();
        }
    }

    public void store(String from, String to, BigDecimal rate) {
        if (from == null || to == null || rate == null) {
            return;
        }
        String key = cacheKey(from, to);
        try {
            redis.opsForValue().set(key, rate, TTL);
        } catch (RedisConnectionFailureException ex) {
            log.warn("Redis store failed for key={}", key, ex);
        }
    }

    private String cacheKey(String from, String to) {
        return from + ":" + to;
    }
}
