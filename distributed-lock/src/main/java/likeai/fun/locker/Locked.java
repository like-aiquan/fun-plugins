package likeai.fun.locker;

import static likeai.fun.StringUtils.requireHasText;

/**
 * @author likeai
 */
@FunctionalInterface
public interface Locked {
    String name();

    default String concat(String key) {
        requireHasText(key);
        return String.format("%s:%s", this.name(), key);
    }
}
