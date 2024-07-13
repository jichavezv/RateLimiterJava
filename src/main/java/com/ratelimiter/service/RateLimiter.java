package com.ratelimiter.service;

import java.time.LocalDateTime;

public interface RateLimiter {
    boolean isAllowed(Long userId, String role);
    UserRequestInfo getUserRequestInfo(Long userId);

    class UserRequestInfo {
        private int requestCount;
        private LocalDateTime lastRequestTime;

        public UserRequestInfo(int requestCount, LocalDateTime lastRequestTime) {
            this.requestCount = requestCount;
            this.lastRequestTime = lastRequestTime;
        }

        public int getRequestCount() {
            return requestCount;
        }

        public void incrementRequests() {
            this.requestCount++;
        }

        public LocalDateTime getLastRequestTime() {
            return lastRequestTime;
        }
    }
}
