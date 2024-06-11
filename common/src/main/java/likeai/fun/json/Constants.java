package likeai.fun.json;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.function.BiFunction;

/**
 * @author likeai
 */
public interface Constants {
    /**
     * json instant with epoch second
     */
    String FORMAT_EPOCH_SECOND = "_FORMAT_EPOCH_SECOND";
    /**
     * json instant with epoch milli
     */
    String FORMAT_EPOCH_MILLI = "_FORMAT_EPOCH_MILLI";

    /**
     * @see com.fasterxml.jackson.datatype.jsr310.DecimalUtils
     */
    default <T> T extractSecondsAndNanos(BigDecimal seconds, BiFunction<Long, Integer, T> convert) {
        // Complexity is here to workaround unbounded latency in some BigDecimal operations.
        //   https://github.com/FasterXML/jackson-databind/issues/2141

        long secondsOnly;
        int nanosOnly;

        BigDecimal nanoseconds = seconds.scaleByPowerOfTen(9);
        if (nanoseconds.precision() - nanoseconds.scale() <= 0) {
            // There are no non-zero digits to the left of the decimal point.
            // This protects against very negative exponents.
            secondsOnly = nanosOnly = 0;
        } else if (seconds.scale() < -63) {
            // There would be no low-order bits once we chop to a long.
            // This protects against very positive exponents.
            secondsOnly = nanosOnly = 0;
        } else {
            // Now we know that seconds has reasonable scale, we can safely chop it apart.
            secondsOnly = seconds.longValue();
            nanosOnly = nanoseconds.subtract(BigDecimal.valueOf(secondsOnly).scaleByPowerOfTen(9)).intValue();

            if (secondsOnly < 0 && secondsOnly > Instant.MIN.getEpochSecond()) {
                // Issue #69 and Issue #120: avoid sending a negative adjustment to the Instant constructor, we want this as the actual nanos
                nanosOnly = Math.abs(nanosOnly);
            }
        }
        return convert.apply(secondsOnly, nanosOnly);
    }
}
