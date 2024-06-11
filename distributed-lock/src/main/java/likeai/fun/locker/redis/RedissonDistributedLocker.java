package likeai.fun.locker.redis;

import java.util.concurrent.TimeUnit;
import likeai.fun.locker.DistributedLock;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author likeai
 */
public class RedissonDistributedLocker implements DistributedLock {
    private final static Logger log = LoggerFactory.getLogger(RedissonDistributedLocker.class);
    private final RLock locker;
    private final String locked;
    private long waitTime = 10;
    private long leaseTime = 30;
    private TimeUnit unit = TimeUnit.SECONDS;

    RedissonDistributedLocker(RedissonClient client, String locked) {
        this.locked = locked;
        this.locker = client.getReadWriteLock(locked).writeLock();
    }

    public RLock getLocker() {
        return locker;
    }

    public String key() {
        return locked;
    }

    public void waitTime(long waitTime) {
        this.waitTime = waitTime;
    }

    public void leaseTime(long leaseTime) {
        this.leaseTime = leaseTime;
    }

    public void unit(TimeUnit unit) {
        this.unit = unit;
    }

    @Override
    public boolean lock() {
        try {
            if (log.isDebugEnabled()) {
                log.debug("Try lock [{}] with [{}{}]", this.locked, this.leaseTime, this.unit);
            }
            return this.locker.tryLock(this.waitTime, this.leaseTime, this.unit);
        } catch (InterruptedException e) {
            // TODO fix it.
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean release() {
        this.locker.unlock();
        if (log.isDebugEnabled()) {
            log.debug("Lock try unlocked success!");
        }
        return true;
    }
}
