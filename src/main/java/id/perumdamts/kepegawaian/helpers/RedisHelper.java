package id.perumdamts.kepegawaian.helpers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
public class RedisHelper {
    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * Generates a random token which is valid for 5 minutes.
     *
     * @return a random token
     */
    public String generateToken() {
        String token = UUID.randomUUID().toString();
        // Set the token for 5 minutes
        redisTemplate.opsForValue().set(token, token, 5, TimeUnit.MINUTES);
        return token;
    }

    public Boolean validateToken(String token) {
        return token == null || !redisTemplate.delete(token);
    }
}
