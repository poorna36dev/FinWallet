package com.poorna.fintech.security;

import java.io.IOException;

import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class RateLimitFilter extends OncePerRequestFilter {

    private final RateLimiterService rateLimiterService;

    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String endpoint = getEndpoint(request);
        String ip = getClientIp(request);
        try {


        if (endpoint != null && !rateLimiterService.isAllowed(endpoint, ip)) {

            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);

            objectMapper.writeValue(
                    response.getWriter(),
                    new ErrorResponse(
                            HttpStatus.TOO_MANY_REQUESTS.value(),
                            "Too many requests. Please try again later."
                    )
            );

            return;
        }
        } catch (RedisConnectionFailureException ex) {
            log.error("Rate limiter unavailable", ex);

            response.setStatus(HttpStatus.SERVICE_UNAVAILABLE.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);

            objectMapper.writeValue(
                response.getWriter(),
                Map.of(
                    "message", "Service temporarily unavailable. Please try again later."
                )
            );

            return;
        }

        filterChain.doFilter(request, response);
    }

    private String getClientIp(HttpServletRequest request) {

        String forwarded = request.getHeader("X-Forwarded-For");

        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }

        return request.getRemoteAddr();
    }

    private String getEndpoint(HttpServletRequest request) {

        String uri = request.getRequestURI();

        if (uri.equals("/auth/login")) {
            return "login";
        }

        if (uri.equals("/auth/register/user")) {
            return "register";
        }

        if (uri.equals("/auth/forgotPassword")) {
            return "forgot-password";
        }

        if (uri.equals("/auth/password-reset")) {
            return "password-reset";
        }

        return null;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {

        return getEndpoint(request) == null;
    }

    private record ErrorResponse(
            int status,
            String message
    ) {
    }
}