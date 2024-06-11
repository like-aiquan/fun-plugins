package likeai.fun.topic;

/**
 * @author chenaiquan
 * @date 2022/6/12 14:05
 */
public abstract class NormalRocketTopic implements RocketTopic {
    @Override
    public String key() {
        // normal topic do not need partition key
        return "normal";
    }
}
