package likeai.fun.locker.renewal;

import io.lettuce.core.ScriptOutputType;
import java.util.concurrent.TimeUnit;
import likeai.fun.locker.redis.RedisDistributedLocker;
import likeai.fun.task.DaleyTaskHolder;
import likeai.fun.task.DaleyTask;
import likeai.fun.redis.RedisLinkTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author likeai
 */
public class RedisDistributedLockerRenewal implements RenewalLockerTask {
    private final static Logger log = LoggerFactory.getLogger(RedisDistributedLockerRenewal.class);
    private static final String renewal =
            """ 
                    if redis.call("GET", KEYS[1]) == ARGV[1] then
                      return redis.call("EXPIRE", KEYS[1], ARGV[2])
                    else
                      return -1
                    end
                    """;
    private final String lock;
    private final String lockId;
    private final long expire;
    private final RedisLinkTemplate connect;

    public RedisDistributedLockerRenewal(RedisDistributedLocker locker) {
        this.lock = locker.getLock();
        this.lockId = locker.getLockId();
        this.expire = locker.getExpire().toMillis();
        this.connect = locker.getConnect();

        // 当过期时间已经过了2/3时触发自动续期
        DaleyTaskHolder.submit(new DaleyTask((this.expire * 2) / 3, TimeUnit.MILLISECONDS, this::renewal));
    }

    private RedisDistributedLockerRenewal(RedisDistributedLockerRenewal renewal) {
        this.lock = renewal.getLock();
        this.lockId = renewal.getLockId();
        this.expire = renewal.getExpire();
        this.connect = renewal.getConnect();

        DaleyTaskHolder.submit(new DaleyTask((this.expire * 2) / 3, TimeUnit.MILLISECONDS, this::renewal));
    }

    @Override
    public void renewal() {
        String[] args = new String[]{this.lockId, String.valueOf(this.expire)};
        Long expired = this.connect.sync().eval(renewal, ScriptOutputType.INTEGER, new String[]{this.lock}, args);
        if (log.isDebugEnabled()) {
            log.debug("renewal key {} id {}, expire {}", this.lock, this.lockId, this.expire);
        }
        // 锁已经释放
        if (expired < 0) {
            return;
        }
        new RedisDistributedLockerRenewal(this);
    }

    public String getLock() {
        return lock;
    }

    public String getLockId() {
        return lockId;
    }

    public long getExpire() {
        return expire;
    }

    public RedisLinkTemplate getConnect() {
        return connect;
    }
}
