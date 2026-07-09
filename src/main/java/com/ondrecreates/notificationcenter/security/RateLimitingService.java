package com.ondrecreates.notificationcenter.security;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory token bucket per klient (Bucket4j) – ne Redis. Appka běží jako
 * jedna instance (žádné horizontální škálování v MVP), takže sdílený stav
 * napříč instancemi není potřeba. Až by šlo o víc instancí za load
 * balancerem, limit by bylo potřeba přesunout do Redis (bucket4j to podporuje
 * jako drop-in rozšíření), jinak by každá instance počítala vlastní limit
 * nezávisle a efektivní limit by byl (N × nastavený limit).
 */
@Component
public class RateLimitingService {

    private final Map<Long, Bucket> buckets = new ConcurrentHashMap<>();
    private final int capacity;
    private final int refillTokens;
    private final Duration refillPeriod;

    public RateLimitingService(@Value("${app.rate-limit.capacity}") int capacity,
                                @Value("${app.rate-limit.refill-tokens}") int refillTokens,
                                @Value("${app.rate-limit.refill-period-seconds}") long refillPeriodSeconds) {
        this.capacity = capacity;
        this.refillTokens = refillTokens;
        this.refillPeriod = Duration.ofSeconds(refillPeriodSeconds);
    }

    public boolean tryConsume(Long clientId) {
        return buckets.computeIfAbsent(clientId, id -> newBucket()).tryConsume(1);
    }

    private Bucket newBucket() {
        Bandwidth limit = Bandwidth.classic(capacity, Refill.greedy(refillTokens, refillPeriod));
        return Bucket.builder().addLimit(limit).build();
    }
}
