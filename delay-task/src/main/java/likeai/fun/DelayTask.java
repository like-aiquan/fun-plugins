package likeai.fun;

import static java.util.Objects.requireNonNull;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DelayTask implements Delayed {
    private static final Logger log = LoggerFactory.getLogger(DelayTask.class);
    /**
     * 时间
     */
    private final long expire;
    /**
     * 任务
     */
    private final Runnable runnable;

    public DelayTask(long delay, TimeUnit unit, Runnable runnable) {
        requireNonNull(runnable);
        log.info("delay [{}, {}]", delay, unit);
        // 任务投递时间
        this.expire = System.currentTimeMillis() + TimeUnit.MILLISECONDS.convert(delay, unit);
        this.runnable = runnable;
    }

    @Override
    public long getDelay(TimeUnit unit) {
        return unit.convert(this.expire - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
    }

    @Override
    public int compareTo(Delayed other) {
        return Long.compare(this.expire, ((DelayTask) other).expire);
    }

    public Runnable runnable() {
        return this.runnable;
    }
}
