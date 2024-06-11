package likeai.fun;


import static java.util.concurrent.TimeUnit.MILLISECONDS;

import com.google.common.base.Stopwatch;

/**
 * from com.google.inject.internal.util
 * </br> A continuously timing stopwatch that is used for simple performance monitoring.
 * </br> Author: crazybob@google.com (Bob Lee)
 */
public class ContinuousStopwatch {
    private final Stopwatch stopwatch;

    /**
     * Constructs a ContinuousStopwatch, which will start timing immediately after construction.
     */
    public ContinuousStopwatch() {
        this.stopwatch = Stopwatch.createUnstarted();
        this.reset();
    }

    /**
     * Constructs a ContinuousStopwatch, which will start timing immediately after construction.
     *
     * @param stopwatch the internal stopwatch used by ContinuousStopwatch
     */
    public ContinuousStopwatch(Stopwatch stopwatch) {
        this.stopwatch = stopwatch;
        this.reset();
    }

    /**
     * Resets and returns elapsed time in milliseconds.
     */
    public long reset() {
        long elapsedTimeMs = this.stopwatch.elapsed(MILLISECONDS);
        this.stopwatch.reset();
        this.stopwatch.start();
        return elapsedTimeMs;
    }

    public long stop() {
        long elapsed = this.stopwatch.elapsed(MILLISECONDS);
        this.stopwatch.stop();
        return elapsed;
    }
}
