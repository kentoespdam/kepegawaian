# Redis Cache Config — Pending Implementation

> Status: **TODO** — Belum diimplementasi. Dicatat agar tidak terlewat.

## Problem

`RedisConfig.java` declare manual `RedisTemplate<String, Object>` + `StringRedisTemplate` tanpa custom serializer — menggunakan default `JdkSerializationRedisSerializer` yang **melanggar CODING_RULES.md**:

> *"NEVER use `JdkSerializationRedisSerializer`. Use `StringRedisSerializer` (keys) + `GenericJackson2JsonRedisSerializer` (values)."*

Spring Boot auto-config sudah provide bean yang sama dengan `@ConditionalOnMissingBean`. Manual bean ini redundant + menyalahi aturan serializer.

## Plan

### 1. Hapus `RedisConfig.java`

Spring Boot auto-config (`DataRedisAutoConfiguration`) sudah provide:
- `RedisTemplate<Object, Object>` dengan `@ConditionalOnMissingBean(name = "redisTemplate")`
- `StringRedisTemplate` dengan `@ConditionalOnMissingBean`

### 2. Tambah `spring.cache` di `application.yml`

```yaml
spring:
  cache:
    type: redis
    redis:
      time-to-live: ${REDIS_CACHE_TTL:600000}   # default 10 menit (ms)
      cache-null-values: false                    # jangan cache null
      key-prefix: "kepegawaian:"                  # sesuai CODING_RULES
      use-key-prefix: true
```

### 3. Tambah `RedisCacheManagerBuilderCustomizer` bean

Per-cache-name TTL (sesuai CODING_RULES: *"Configure per-cache TTL in `RedisCacheManagerBuilderCustomizer`"*):

```java
@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public RedisCacheManagerBuilderCustomizer cacheManagerCustomizer() {
        return builder -> builder
            .withCacheConfiguration("master",
                RedisCacheConfiguration.defaultCacheConfig()
                    .entryTtl(Duration.ofMinutes(30)))    // master data jarang berubah
            .withCacheConfiguration("pegawai",
                RedisCacheConfiguration.defaultCacheConfig()
                    .entryTtl(Duration.ofMinutes(5)));    // data pegawai sering update
    }
}
```

### 4. Tambah `CacheErrorHandler`

Sesuai CODING_RULES: *"Configure `CacheErrorHandler` — Redis down degrades to DB, not 500 error."*

```java
@Bean
public CacheErrorHandler cacheErrorHandler() {
    return new SimpleCacheErrorHandler() {
        @Override
        public void handleCacheGetError(RuntimeException exception, Cache cache, Object key) {
            log.warn("Redis GET failed for key {}: {}", key, exception.getMessage());
        }
        // ... handle put, evict, clear errors — log & degrade
    };
}
```

### 5. Environment Variables

Tambah ke `env.example`:
```
REDIS_CACHE_TTL=600000    # default 10 menit (ms)
```

## Notes

- Saat ini **tidak ada `@Cacheable`/`@CacheEvict`** di codebase — config ini persiapan
- `RedisHelper` pakai `StringRedisTemplate` langsung (opsForValue().set()) — tidak terpengaruh
- `key-prefix: "kepegawaian:"` sesuai CODING_RULES: `kepegawaian:<domain>:<identifier>`
- `cache-null-values: false` — hindari caching null (cache stampede)
