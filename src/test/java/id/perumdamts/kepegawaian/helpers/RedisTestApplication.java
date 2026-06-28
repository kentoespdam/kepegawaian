package id.perumdamts.kepegawaian.helpers;

import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Minimal config anchor for @DataRedisTest in this package, so the slice does not
 * climb to KepegawaianApplication (which carries @EnableJpaRepositories and would
 * drag JPA into a Redis-only test). Component scan is rooted at this package.
 */
@SpringBootApplication
class RedisTestApplication {
}
