package com.example.terravista.config;

import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class RateLimitConfig {

    @Bean
    public RateLimiterRegistry rateLimiterRegistry() {
        RateLimiterConfig config = RateLimiterConfig.custom()
                .limitRefreshPeriod(Duration.ofMinutes(1)) // 限流周期
                .limitForPeriod(100)                       // 每个周期允许的请求数
                .timeoutDuration(Duration.ofMillis(500))   // 等待令牌的超时时间
                .build();

        return RateLimiterRegistry.of(config);
    }

    @Bean(name = "apiRateLimiter")
    public RateLimiter apiRateLimiter(RateLimiterRegistry registry) {
        return registry.rateLimiter("apiRateLimiter");
    }
}