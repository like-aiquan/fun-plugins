import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * @author likeai
 */
public class RedisTemplateTest {
    private final static Logger log = LoggerFactory.getLogger(RedisTemplateTest.class);

    RedisTemplate<String, String> redisTemplate;

    @BeforeEach
    void setRedisTemplate() {
        LettuceConnectionFactory factory = new LettuceConnectionFactory(new RedisStandaloneConfiguration());
        factory.afterPropertiesSet();
        RedisSerializer<String> stringSerializer = new StringRedisSerializer();
        RedisSerializer<Object> jsonSerializer = new Jackson2JsonRedisSerializer<>(Object.class);
        this.redisTemplate = new RedisTemplate<>();
        this.redisTemplate.setConnectionFactory(factory);
        this.redisTemplate.setDefaultSerializer(jsonSerializer);
        this.redisTemplate.setKeySerializer(stringSerializer);
        this.redisTemplate.setValueSerializer(jsonSerializer);
        this.redisTemplate.setHashKeySerializer(stringSerializer);
        this.redisTemplate.setHashValueSerializer(jsonSerializer);
        this.redisTemplate.afterPropertiesSet();
    }

    @Test
    void testRedisTemplate() {
        log.info("{}", this.redisTemplate.hasKey("Test"));
        this.redisTemplate.opsForValue().set("Test", "likeai", 20, TimeUnit.SECONDS);

        log.info("{}-- {}s", this.redisTemplate.hasKey("Test"), this.redisTemplate.getExpire("Test", TimeUnit.SECONDS));
        sleep(1000);
        log.info("{}-- {}s", this.redisTemplate.hasKey("Test"), this.redisTemplate.getExpire("Test", TimeUnit.SECONDS));
        sleep(1000);
        log.info("{}-- {}s", this.redisTemplate.hasKey("Test"), this.redisTemplate.getExpire("Test", TimeUnit.SECONDS));
        sleep(1000);
        log.info("{}-- {}s", this.redisTemplate.hasKey("Test"), this.redisTemplate.getExpire("Test", TimeUnit.SECONDS));
        sleep(1000);
        log.info("{}-- {}s", this.redisTemplate.hasKey("Test"), this.redisTemplate.getExpire("Test", TimeUnit.SECONDS));
        sleep(1000);
        log.info("{}-- {}s", this.redisTemplate.hasKey("Test"), this.redisTemplate.getExpire("Test", TimeUnit.SECONDS));
        sleep(1000);
        log.info("{}-- {}s", this.redisTemplate.hasKey("Test"), this.redisTemplate.getExpire("Test", TimeUnit.SECONDS));
        sleep(20000);
        log.info("{}-- {}s", this.redisTemplate.hasKey("Test"), this.redisTemplate.getExpire("Test", TimeUnit.SECONDS));
    }

    private void sleep(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            log.error("InterruptedException {}", e.getMessage(), e);
        }
    }
}
