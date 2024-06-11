package likeai.fun.task;

import java.util.Objects;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author likeai
 */
public class DaleyTaskHolder {
    private static final Logger log = LoggerFactory.getLogger(DaleyTaskHolder.class);
    protected static final DelayQueue<DaleyTask> tasks = new DelayQueue<>();
    protected static final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    static {
        // 创建单线程线程池, 定时处理
        executor.scheduleWithFixedDelay(runTask(), 0, 500, TimeUnit.MILLISECONDS);
    }

    private static Runnable runTask() {
        return () -> {
            if (log.isDebugEnabled()) {
                log.debug("run executor task");
            }
            DaleyTask task = tasks.poll();
            if (Objects.isNull(task)) {
                return;
            }
            if (log.isDebugEnabled()) {
                log.debug("poll task and run task");
            }
            try {
                task.runnable().run();
            } catch (Throwable e) {
                log.error(e.getMessage(), e);
            }
            if (log.isDebugEnabled()) {
                log.debug("finish task");
            }
        };
    }

    public static void submit(DaleyTask task) {
        tasks.add(task);
        if (log.isDebugEnabled()) {
            log.debug("add task success daley {}", task.getDelay(TimeUnit.SECONDS));
        }
    }
}
