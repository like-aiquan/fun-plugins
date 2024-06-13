package likeai.fun;

import likeai.fun.consumer.AbstractNormalRocketConsumer;
import likeai.fun.mq.Action;
import likeai.fun.mq.Message;
import likeai.fun.mq.RocketMqConfig;
import likeai.fun.mq.SubscribeRelation.Builder;
import org.springframework.stereotype.Component;

/**
 * @author likeai
 */
@Component
public class NoTopicTestConsumer extends AbstractNormalRocketConsumer<TestNormalTopic> {

    private static final String TOPIC = TestNormalTopic.TOPIC_NAME;
    private static final String GROUP = "no-topic-test-consume";

    public NoTopicTestConsumer(RocketMqConfig config) {
        super(config, Builder.newBuilder().topic(TOPIC).group(GROUP).build(), TestNormalTopic.class);
    }

    @Override
    public boolean accept(Message message, TestNormalTopic topic) {
        return true;
    }

    @Override
    public Action consume(Message message, TestNormalTopic topic) {
        return Action.Commit;
    }
}