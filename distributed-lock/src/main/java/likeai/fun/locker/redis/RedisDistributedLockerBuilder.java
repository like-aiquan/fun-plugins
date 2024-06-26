package likeai.fun.locker.redis;

import static likeai.fun.StringUtils.hasText;
import static likeai.fun.StringUtils.requireHasText;

import io.lettuce.core.RedisClient;
import java.time.Duration;
import likeai.fun.redis.RedisLinkTemplate;

/**
 * @author likeai
 */
public class RedisDistributedLockerBuilder {
    private String prefix = "d-lock";
    private String app;
    private String key;
    private final RedisDistributedLocker redisDistributedLocker;

    private RedisDistributedLockerBuilder(RedisLinkTemplate connect) {
        this.redisDistributedLocker = new RedisDistributedLocker(connect);
    }

    public static RedisDistributedLockerBuilder builder(RedisLinkTemplate connect) {
        return new RedisDistributedLockerBuilder(connect);
    }

    public static RedisDistributedLockerBuilder builder(RedisClient client) {
        return new RedisDistributedLockerBuilder(new RedisLinkTemplate(client));
    }

    public RedisDistributedLockerBuilder app(String app) {
        this.app = app;
        return this;
    }

    public RedisDistributedLockerBuilder prefix(String prefix) {
        this.prefix = prefix;
        return this;
    }

    public RedisDistributedLockerBuilder key(String key) {
        this.key = key;
        return this;
    }

    public RedisDistributedLockerBuilder wait(Duration wait) {
        this.redisDistributedLocker.setWait(wait);
        return this;
    }

    public RedisDistributedLockerBuilder expire(Duration expire) {
        this.redisDistributedLocker.setExpire(expire);
        return this;
    }

    public RedisDistributedLockerBuilder retry(Duration retry) {
        this.redisDistributedLocker.setRetry(retry);
        return this;
    }

    public RedisDistributedLockerBuilder lockId(String lockId) {
        this.redisDistributedLocker.setLockId(lockId);
        return this;
    }

    public RedisDistributedLocker build() {
        requireHasText(this.prefix, "Require has a prefix");
        requireHasText(this.key, "Require has a lock key");

        String locked = hasText(this.app)
                ? String.format("%s:%s:%s", this.prefix, this.app, this.key)
                : String.format("%s:%s", this.prefix, this.key);
        this.redisDistributedLocker.setLock(locked);
        return this.redisDistributedLocker;
    }
}
