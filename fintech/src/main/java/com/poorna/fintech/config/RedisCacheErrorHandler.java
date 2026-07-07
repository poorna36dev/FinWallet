package com.poorna.fintech.config;

import org.springframework.cache.Cache;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class RedisCacheErrorHandler implements CacheErrorHandler {


    @Override
    public void handleCacheGetError(RuntimeException exception,
                                    Cache cache,
                                    Object key) {

        log.warn("Redis GET failed for key {}. Falling back to database.",
                key, exception);
    }

    @Override
    public void handleCachePutError(RuntimeException exception,
                                    Cache cache,
                                    Object key,
                                    Object value) {

        log.warn("Redis PUT failed for key {}.", key, exception);
    }

    @Override
    public void handleCacheEvictError(RuntimeException exception,
                                      Cache cache,
                                      Object key) {

        log.warn("Redis EVICT failed for key {}.", key, exception);
    }

    @Override
    public void handleCacheClearError(RuntimeException exception,
                                      Cache cache) {

        log.warn("Redis CLEAR failed.", exception);
    }
}
