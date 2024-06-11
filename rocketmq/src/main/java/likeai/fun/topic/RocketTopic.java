package likeai.fun.topic;

import com.google.common.collect.Lists;
import java.util.List;

/**
 * @author chenaiquan
 * @date 2022/6/12 14:04
 */
public interface RocketTopic {
    String topicName();

    String key();

    default int delayTimeLevel() {
        return 0;
    }

    default List<String> tags() {
        return Lists.newArrayList("*");
    }

    default boolean orderTopic() {
        return false;
    }

    default boolean logBody() {
        return true;
    }
}
