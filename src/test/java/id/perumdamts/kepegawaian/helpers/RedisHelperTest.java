package id.perumdamts.kepegawaian.helpers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class RedisHelperTest {
    @Autowired
    private RedisHelper redisHelper;
    @Autowired
    private StringRedisTemplate redisTemplate;

    @Test
    public void getToken() {
        String token = redisHelper.generateToken();
        System.out.println(token);
//        assertTrue(redisHelper.validateToken(token));
        String redisToken = redisTemplate.opsForValue().get(token);
        assertEquals(token, redisToken);
        System.out.println(redisHelper.validateToken(redisToken));
    }
}