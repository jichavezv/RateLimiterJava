package com.ratelimiter.service.impl;

import com.ratelimiter.entity.UserRequestInfo;
import com.ratelimiter.service.RateLimiter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class RateLimiterImpl implements RateLimiter {
    private final int regularLimit;
    private final int premiumLimit;
    private final long timeWindowInSeconds;
    private final int maxConsecutive429;
    private final long blockTimeInSeconds;
    private final Map<Long, UserRequestInfo> userRequestMap = new HashMap<>();

    public RateLimiterImpl(
            @Value("${ratelimiter.regularLimit}") int regularLimit,
            @Value("${ratelimiter.premiumLimit}") int premiumLimit,
            @Value("${ratelimiter.timeWindowInSeconds}") long timeWindowInSeconds,
            @Value("${ratelimiter.maxConsecutive429}") int maxConsecutive429,
            @Value("${ratelimiter.blockTimeInSeconds}") long blockTimeInSeconds) {
        this.regularLimit = regularLimit;
        this.premiumLimit = premiumLimit;
        this.timeWindowInSeconds = timeWindowInSeconds;
        this.maxConsecutive429 = maxConsecutive429;
        this.blockTimeInSeconds = blockTimeInSeconds;
    }

    @Override
    public synchronized boolean isAllowed(Long userId, String role) {
        LocalDateTime now = LocalDateTime.now();
        UserRequestInfo userRequestInfo = userRequestMap.getOrDefault(userId, new UserRequestInfo(0, now, 0, null));

        // Verificar si el usuario está bloqueado
        if (userRequestInfo.getBlockEndTime() != null && userRequestInfo.getBlockEndTime().isAfter(now)) {
            return false;
        }

        int limit = "premium".equalsIgnoreCase(role) ? premiumLimit : regularLimit;

        // Reiniciar la cuenta de requests si ha pasado el tiempo de la ventana
        if (userRequestInfo.getLastRequestTime().plusSeconds(timeWindowInSeconds).isBefore(now)) {
            userRequestInfo = new UserRequestInfo(1, now, 0, null);
        } else {
            userRequestInfo.incrementRequests();
        }

        // Verificar si el usuario ha excedido el límite de requests
        if (userRequestInfo.getRequestCount() > limit) {
            userRequestInfo.incrementConsecutive429s();
            if (userRequestInfo.getConsecutive429s() >= maxConsecutive429) {
                userRequestInfo.setBlockEndTime(now.plusSeconds(blockTimeInSeconds));
            }
            userRequestMap.put(userId, userRequestInfo);
            return false;
        }

        userRequestInfo.resetConsecutive429s();
        userRequestMap.put(userId, userRequestInfo);
        return true;
    }

    @Override
    public UserRequestInfo getUserRequestInfo(Long userId) {
        return userRequestMap.get(userId);
    }
}

