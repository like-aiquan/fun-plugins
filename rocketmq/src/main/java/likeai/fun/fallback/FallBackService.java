package likeai.fun.fallback;


import likeai.fun.topic.RocketTopic;

/**
 * @author chenaiquan
 * @date 2022/6/14 21:56
 */
public interface FallBackService {
    void fallBack(RocketTopic topic, Throwable e);
}
