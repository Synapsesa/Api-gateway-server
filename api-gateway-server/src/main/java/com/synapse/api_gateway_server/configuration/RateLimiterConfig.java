package com.synapse.api_gateway_server.configuration;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.scripting.support.ResourceScriptSource;

import com.synapse.api_gateway_server.ratelimit.limit.CustomRedisRateLimiter;
import com.synapse.api_gateway_server.ratelimit.limit.RateLimiter;

@Configuration
public class RateLimiterConfig {
    @Bean
    public RateLimiter RateLimiter(
        ReactiveStringRedisTemplate redisTemplate,
        RedisScript<List<Long>> redisScript
    ) {
        return new CustomRedisRateLimiter(redisTemplate, redisScript);
    }

    @Bean
    @SuppressWarnings("unchecked")
    public RedisScript<List<Long>> customRedisRequestRateLimiterScript() {
        DefaultRedisScript<List<Long>> redisScript = new DefaultRedisScript<>();
        ClassPathResource resource = new ClassPathResource("script/rate_limiter.lua");
        if (!resource.exists()) {
            throw new IllegalArgumentException("script/rate_limiter.lua not found");
        }
        redisScript.setScriptSource(new ResourceScriptSource(resource));
        redisScript.setResultType((Class<List<Long>>) (Class<?>) List.class);
        return redisScript;
    }
}
