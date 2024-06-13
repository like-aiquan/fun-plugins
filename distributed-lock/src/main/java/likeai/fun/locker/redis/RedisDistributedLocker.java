package likeai.fun.locker.redis;

import static java.lang.System.currentTimeMillis;
import static java.util.Objects.nonNull;
import static likeai.fun.StringUtils.hasText;

import io.lettuce.core.ScriptOutputType;
import io.lettuce.core.SetArgs.Builder;
import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import likeai.fun.locker.DistributedLock;
import likeai.fun.locker.renewal.RedisDistributedLockerRenewal;
import likeai.fun.redis.RedisLinkTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author likeai
 */
public class RedisDistributedLocker implements DistributedLock {
    private static final Logger log = LoggerFactory.getLogger(RedisDistributedLocker.class);
    private final RedisLinkTemplate connect;
    private Duration wait = Duration.ZERO;
    private Duration retry = Duration.ofMillis(200);
    private Duration expire = Duration.ofSeconds(10);
    private final AtomicBoolean locked = new AtomicBoolean(false);
    private String lock;
    private String lockId = UUID.randomUUID().toString().replace("-", "");
    private String release =
            """
                    if redis.call("get",KEYS[1]) == ARGV[1] then
                        return redis.call("del",KEYS[1])
                    else
                        return 0
                    end
                    """;

    RedisDistributedLocker(RedisLinkTemplate connect) {
        this.connect = connect;
    }

    public boolean lock() {
        if (!this.locked.compareAndSet(false, true)) {
            log.debug("repeat lock!");
            return true;
        }

        if (log.isDebugEnabled()) {
            log.debug("try lock [k:{},v:{},e:{},w:{},r:{}]", this.locked, this.lockId, expire, wait, retry);
        }

        if (this.wait.equals(Duration.ZERO)) {
            return this.doLock();
        }

        long wait = currentTimeMillis() + this.wait.toMillis();
        while (wait > currentTimeMillis()) {
            if (this.doLock()) {
                return true;
            }

            try {
                Thread.sleep(this.retry.toMillis());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.error("Thread Interrupted! {}", e.getMessage(), e);
                return false;
            }
        }
        return false;
    }

    private boolean doLock() {
        String r = this.connect.sync().set(this.lock, this.lockId, Builder.nx().px(this.expire));
        if (log.isDebugEnabled()) {
            log.debug("do lock response [{},{},{}]", lock, lockId, r);
        }
        boolean locked = "OK".equals(r);
        if (locked) {
            new RedisDistributedLockerRenewal(this);
        }
        return locked;
    }

    @Override
    public boolean release() {
        if (!this.locked.get()) {
            throw new IllegalStateException("lock " + this.lock + " not locked!");
        }
        if (!this.locked.compareAndSet(true, false)) {
            log.warn("already released {} lock!", lock);
            return true;
        }
        Long r = this.connect.sync().eval(release, ScriptOutputType.INTEGER, new String[]{lock}, lockId);
        if (log.isDebugEnabled()) {
            log.debug("release lock [{},{},{}]", lock, lockId, r);
        }
        return nonNull(r) && r == 1;
    }

    void setWait(Duration wait) {
        this.wait = wait;
    }

    void setExpire(Duration expire) {
        this.expire = expire;
    }

    void setLock(String lock) {
        this.lock = lock;
    }

    void releaseLua(String lockId, String release) {
        this.lockId = lockId;
        if (!hasText(release)) {
            this.release = release;
        }
    }

    void setRetry(Duration retry) {
        this.retry = retry;
    }

    public Duration getExpire() {
        return expire;
    }

    public String getLock() {
        return lock;
    }

    public String getLockId() {
        return lockId;
    }

    public RedisLinkTemplate getConnect() {
        return connect;
    }
}
