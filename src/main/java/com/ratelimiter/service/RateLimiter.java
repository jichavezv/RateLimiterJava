package com.ratelimiter.service;

import com.ratelimiter.entity.UserRequestInfo;

public interface RateLimiter {
    boolean isAllowed(Long userId, String role);

    UserRequestInfo getUserRequestInfo(Long userId);
    
    boolean isBlocked(Long userId);
}