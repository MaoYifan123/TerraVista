package com.example.terravista.config;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.time.Duration;

@Configuration
public class RateLimitConfig{

    @Bean
    public RateLimiterRegistry rateLimiterRegistry() {
        RateLimiterConfig config = RateLimiterConfig.custom()
                .limitRefreshPeriod(Duration.ofSeconds(10)) // 10秒周期
                .limitForPeriod(2)                         // 允许2次请求
                .timeoutDuration(Duration.ofMillis(500))   // 超时时间
                .build();

        RateLimiterRegistry registry = RateLimiterRegistry.of(config);
        // 日志监听
        registry.rateLimiter("apiRateLimiter")
                .getEventPublisher()
                .onSuccess(event -> System.out.println("RateLimiter [ALLOWED]"))
                .onFailure(event -> System.out.println("RateLimiter [BLOCKED]"));
        return registry;
    }

    @Bean(name = "apiRateLimiter")
    public RateLimiter apiRateLimiter(RateLimiterRegistry registry) {
        return registry.rateLimiter("apiRateLimiter");
    }
}