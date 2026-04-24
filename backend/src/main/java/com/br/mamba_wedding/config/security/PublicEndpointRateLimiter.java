package com.br.mamba_wedding.config.security;

import com.br.mamba_wedding.common.exception.TooManyRequestsException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class PublicEndpointRateLimiter {

    private final ConcurrentHashMap<String, WindowState> windows = new ConcurrentHashMap<>();

    public void assertAllowed(HttpServletRequest request, String action, String subject, int maxRequests, Duration window) {
        String key = buildKey(request, action, subject);
        Instant now = Instant.now();
        AtomicBoolean allowed = new AtomicBoolean(false);

        WindowState state = windows.compute(key, (ignored, currentState) -> {
            if (currentState == null || currentState.windowStart().plus(window).isBefore(now)) {
                allowed.set(true);
                return new WindowState(now, 1);
            }

            if (currentState.requestCount() >= maxRequests) {
                return currentState;
            }

            allowed.set(true);
            return new WindowState(currentState.windowStart(), currentState.requestCount() + 1);
        });

        if (!allowed.get() && state.requestCount() >= maxRequests && state.windowStart().plus(window).isAfter(now)) {
            throw new TooManyRequestsException("Muitas tentativas. Tente novamente em instantes.");
        }
    }

    private String buildKey(HttpServletRequest request, String action, String subject) {
        return action + ":" + getClientIp(request) + ":" + normalize(subject);
    }

    private String getClientIp(HttpServletRequest request) {
        String forwardedFor = request.getHeader("X-Forwarded-For");
        if (forwardedFor != null && !forwardedFor.isBlank()) {
            return forwardedFor.split(",")[0].trim();
        }

        String realIp = request.getHeader("X-Real-IP");
        if (realIp != null && !realIp.isBlank()) {
            return realIp.trim();
        }

        return request.getRemoteAddr();
    }

    private String normalize(String value) {
        if (value == null || value.isBlank()) {
            return "unknown";
        }

        return value.trim().toLowerCase();
    }

    private record WindowState(Instant windowStart, int requestCount) {}
}