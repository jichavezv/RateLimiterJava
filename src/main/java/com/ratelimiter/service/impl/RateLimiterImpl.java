package com.ratelimiter.service.impl;

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
    private final Map<Long, UserRequestInfo> userRequestMap = new HashMap<>();

    public RateLimiterImpl(
            @Value("${ratelimiter.regularLimit}") int regularLimit,
            @Value("${ratelimiter.premiumLimit}") int premiumLimit,
            @Value("${ratelimiter.timeWindowInSeconds}") long timeWindowInSeconds) {
        this.regularLimit = regularLimit;
        this.premiumLimit = premiumLimit;
        this.timeWindowInSeconds = timeWindowInSeconds;
    }

    @Override
    public boolean isAllowed(Long userId, String role) {
        LocalDateTime now = LocalDateTime.now();
        UserRequestInfo userRequestInfo = userRequestMap.getOrDefault(userId, new UserRequestInfo(0, now));

        int limit = "premium".equalsIgnoreCase(role) ? premiumLimit : regularLimit;

        if (userRequestInfo.getLastRequestTime().plusSeconds(timeWindowInSeconds).isBefore(now)) {
            userRequestInfo = new UserRequestInfo(1, now);
        } else {
            userRequestInfo.incrementRequests();
        }

        userRequestMap.put(userId, userRequestInfo);
        return userRequestInfo.getRequestCount() <= limit;
    }

    @Override
    public UserRequestInfo getUserRequestInfo(Long userId) {
        return userRequestMap.get(userId);
    }
}
