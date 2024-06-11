package likeai.fun.locker;

import java.util.function.Consumer;

/**
 * @author likeai
 */
public interface DistributedLock {
    boolean lock();

    boolean release();

    default void execute(Consumer<Boolean> command) {
        try {
            command.accept(this.lock());
        } finally {
            this.release();
        }
    }
}
