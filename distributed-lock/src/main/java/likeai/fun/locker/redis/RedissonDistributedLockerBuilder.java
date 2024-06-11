package likeai.fun.locker.redis;

import static java.util.Objects.isNull;
import static java.util.Objects.requireNonNull;
import static likeai.fun.StringUtils.hasText;
import static likeai.fun.StringUtils.requireHasText;

import java.util.concurrent.TimeUnit;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

/**
 * @author likeai
 */
public class RedissonDistributedLockerBuilder {
    private RedissonClient client;
    private String address;
    private String app;
    private String prefix = "d-lock";
    private String key;
    private RedissonDistributedLocker locker;

    RedissonDistributedLockerBuilder(String address) {
        this.address = address;
    }

    RedissonDistributedLockerBuilder(RedissonClient client) {
        this.client = client;
    }

    /**
     * For only one redis instance
     */
    public static RedissonDistributedLockerBuilder builder(String address) {
        requireHasText(address);
        return new RedissonDistributedLockerBuilder(address);
    }

    /**
     * If you have more than one redis instance, please give builder a created RedissonClient
     * I don’t know the implementation principle of redisson cluster server
     * So I simply encapsulated it into a form that I think is easy to use.
     */
    public RedissonDistributedLockerBuilder builder(RedissonClient client) {
        requireNonNull(client);
        return new RedissonDistributedLockerBuilder(client);
    }

    public RedissonDistributedLockerBuilder app(String app) {
        this.app = app;
        return this;
    }

    public RedissonDistributedLockerBuilder prefix(String prefix) {
        this.prefix = prefix;
        return this;
    }

    public RedissonDistributedLockerBuilder key(String key) {
        this.key = key;
        return this;
    }

    public RedissonDistributedLocker build() {
        requireHasText(this.prefix, "Require has a prefix");
        requireHasText(this.key, "Require has a lock key");
        String locked = hasText(this.app)
                ? String.format("%s:%s:%s", this.app, this.prefix, this.key)
                : String.format("%s:%s", this.prefix, this.key);

        if (isNull(this.client)) {
            Config config = new Config();
            config.useSingleServer().setAddress(this.address);
            this.locker = new RedissonDistributedLocker(Redisson.create(config), locked);
        } else {
            this.locker = new RedissonDistributedLocker(this.client, locked);
        }
        return this.locker;
    }

    public RedissonDistributedLockerBuilder waitTime(long waitTime) {
        this.locker.waitTime(waitTime);
        return this;
    }

    public RedissonDistributedLockerBuilder leaseTime(long leaseTime) {
        this.locker.leaseTime(leaseTime);
        return this;
    }

    public RedissonDistributedLockerBuilder unit(TimeUnit unit) {
        this.locker.unit(unit);
        return this;
    }
}
