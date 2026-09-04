package id.perumdamts.kepegawaian.config;

import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.cache.Cache;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public CacheErrorHandler cacheErrorHandler() {
        return new CacheErrorHandler() {
            @Override
            public void handleCacheGetError(RuntimeException exception, Cache cache, Object key) {
                log.warn("Redis cache get failed [cache={}, key={}]: {}", cache.getName(), key, exception.getMessage());
            }

            @Override
            public void handleCachePutError(RuntimeException exception, Cache cache, Object key, @Nullable Object value) {
                log.warn("Redis cache put failed [cache={}, key={}]: {}", cache.getName(), key, exception.getMessage());
            }

            @Override
            public void handleCacheEvictError(RuntimeException exception, Cache cache, Object key) {
                log.warn("Redis cache evict failed [cache={}, key={}]: {}", cache.getName(), key, exception.getMessage());
            }

            @Override
            public void handleCacheClearError(RuntimeException exception, Cache cache) {
                log.warn("Redis cache clear failed [cache={}]: {}", cache.getName(), exception.getMessage());
            }
        };
    }

    @Bean
    public org.springframework.cache.CacheManager cacheManager(
            org.springframework.beans.factory.ObjectProvider<org.springframework.data.redis.connection.RedisConnectionFactory> redisConnectionFactory) {
        org.springframework.data.redis.connection.RedisConnectionFactory factory = redisConnectionFactory.getIfAvailable();
        if (factory != null) {
            org.springframework.data.redis.cache.RedisCacheConfiguration config = org.springframework.data.redis.cache.RedisCacheConfiguration.defaultCacheConfig()
                    .entryTtl(java.time.Duration.ofMillis(600000))
                    .disableCachingNullValues()
                    .computePrefixWith(cacheName -> "kepegawaian:" + cacheName + "::");
            return org.springframework.data.redis.cache.RedisCacheManager.builder(factory)
                    .cacheDefaults(config)
                    .build();
        }
        log.info("RedisConnectionFactory not available, using ConcurrentMapCacheManager");
        return new org.springframework.cache.concurrent.ConcurrentMapCacheManager("gaji-referensi");
    }
}
