import io.lettuce.core.RedisClient;
import io.reactivex.rxjava3.functions.Action;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import likeai.fun.locker.LockedKey;
import likeai.fun.locker.redis.RedisDistributedLocker;
import likeai.fun.locker.redis.RedisDistributedLockerBuilder;
import likeai.fun.locker.redis.RedissonDistributedLocker;
import likeai.fun.locker.redis.RedissonDistributedLockerBuilder;
import likeai.fun.redis.RedisLinkTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RReadWriteLock;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RedisLockerTest {
    private final static Logger log = LoggerFactory.getLogger(RedisLockerTest.class);

    RReadWriteLock locker;
    RedissonClient client;

    @BeforeEach
    void before() {
        Config config = new Config();
        config.useSingleServer().setAddress("redis://localhost:6379");
        this.client = Redisson.create(config);
        this.locker = this.client.getReadWriteLock("TEST");
    }

    @Test
    void test() throws InterruptedException {
        RedisDistributedLocker redisDistributedLocker =
                RedisDistributedLockerBuilder.builder(RedisClient.create("redis://localhost:6379"))
                        .app("APP")
                        .key(LockedKey._REDIS.concat("distributed-lock"))
                        .expire(Duration.of(13, TimeUnit.SECONDS.toChronoUnit()))
                        .build();
        redisDistributedLocker.execute((accept) -> {
            if (!accept) {
                log.info("not accept!");
                return;
            }
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                log.error(e.getMessage(), e);
            }
            log.info("我是sb.");
        });

        Thread.sleep(60000);
    }

    @Test
    void redissonLocker() throws InterruptedException {
        RLock wLock = this.locker.writeLock();
        boolean tried = wLock.tryLock(10, 30, TimeUnit.SECONDS);
        if (!tried) {
            log.error("locked failed!");
        }
        wLock.unlock();
    }

    @Test
    void customerRedissonLocker() {
        RedissonDistributedLocker redissonLocker =
                RedissonDistributedLockerBuilder.builder("redis://localhost:6379")
                        .app("APP")
                        .key(LockedKey._REDISSON.concat("distributed-lock"))
                        .build();
        redissonLocker.execute((accept) -> {
            if (!accept) {
                log.info("not accept!");
                return;
            }
            log.info("Redisson locker test finished");
        });
    }

    @Test
    void redissonRLocker1() throws InterruptedException {
        RLock ttt = this.client.getLock("ttt");
        Runnable action = () -> {
            try {
                log.info("[async], TTT locked [{}]", ttt.tryLock(0, TimeUnit.SECONDS));
            } catch (InterruptedException e) {
                log.error(e.getMessage());
            }
        };
        for (int i = 0; i < 10; i++) {
            new Thread(action).start();
        }
        Thread.sleep(100000);
    }

    @Test
    void redissonRLocker2() throws InterruptedException {
        RLock ttt = this.client.getLock("ttt");
        Runnable action = () -> {
            try {
                log.info("[async], TTT locked [{}]", ttt.tryLock(0, TimeUnit.SECONDS));
            } catch (InterruptedException e) {
                log.error(e.getMessage());
            }
        };
        for (int i = 0; i < 10; i++) {
            new Thread(action).start();
        }
        Thread.sleep(100000);
    }
}