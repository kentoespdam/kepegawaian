package id.perumdamts.kepegawaian.helpers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.redis.test.autoconfigure.DataRedisTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static org.junit.jupiter.api.Assertions.*;

@DataRedisTest
@Testcontainers
@Import(RedisHelper.class)
class RedisHelperTest {
    @Container
    @ServiceConnection(name = "redis")
    static GenericContainer<?> redis =
            new GenericContainer<>(DockerImageName.parse("redis:7-alpine"))
                    .withExposedPorts(6379);

    @Autowired
    private RedisHelper redisHelper;
    @Autowired
    private StringRedisTemplate redisTemplate;

    @Test
    public void getToken() {
        String token = redisHelper.generateToken();
        assertNotNull(token);

        // Verify token exists in Redis
        String redisToken = redisTemplate.opsForValue().get(token);
        assertEquals(token, redisToken);

        // First validation should be successful (returns false, meaning NOT duplicate)
        assertFalse(redisHelper.isTokenAlreadyUsed(token));

        // Second validation should detect a duplicate/invalid token (returns true)
        assertTrue(redisHelper.isTokenAlreadyUsed(token));

        // Verify token is indeed removed from Redis
        assertNull(redisTemplate.opsForValue().get(token));
    }
}
