package com.ratelimiter.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class UserRequestInfo {
    private int requestCount;
    private LocalDateTime lastRequestTime;
    private int consecutive429s;
    private LocalDateTime blockEndTime;

    public void incrementRequests() {
        this.requestCount++;
    }

    public void incrementConsecutive429s() {
        this.consecutive429s++;
    }

    public void resetConsecutive429s() {
        this.consecutive429s = 0;
    }
}
