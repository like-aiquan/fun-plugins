package likeai.fun.consumer;


import com.google.common.base.Stopwatch;
import java.nio.charset.StandardCharsets;
import java.util.List;
import javax.annotation.PreDestroy;
import likeai.fun.ContinuousStopwatch;
import likeai.fun.mq.RocketMqConfig;
import likeai.fun.json.JsonUtil;
import likeai.fun.mq.Action;
import likeai.fun.mq.Message;
import likeai.fun.mq.SubscribeRelation;
import likeai.fun.topic.NormalRocketTopic;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageExt;

/**
 * @author chenaiquan
 * @date 2022/6/13 21:12
 */
public abstract class AbstractNormalRocketConsumer<T extends NormalRocketTopic> extends AbstractRocketConsumer<T> implements MessageListenerConcurrently {

    private final DefaultMQPushConsumer consumer;
    private final SubscribeRelation subscribeRelation;
    private final Class<T> bindClazz;

    public AbstractNormalRocketConsumer(RocketMqConfig rocketMqConfig, SubscribeRelation subscribeRelation, Class<T> bindClazz) {
        this(rocketMqConfig, subscribeRelation, bindClazz, new DefaultMQPushConsumer());
    }

    public AbstractNormalRocketConsumer(RocketMqConfig rocketMqConfig, SubscribeRelation subscribeRelation,
            Class<T> bindClazz, DefaultMQPushConsumer consumer) {
        super(rocketMqConfig);
        this.bindClazz = bindClazz;
        this.subscribeRelation = subscribeRelation;
        this.consumer = consumer;
    }

    public void start() throws MQClientException {
        this.consumer.setNamespace(this.rocketMqConfig.getNameSpace());
        String nameSrvAddr = this.rocketMqConfig.getNameSrvAddr();
        if (nameSrvAddr != null && !nameSrvAddr.isEmpty()) {
            this.consumer.setNamesrvAddr(nameSrvAddr);
        }
        String topic = this.resolveTopicGroupName(this.subscribeRelation.topic());
        String group = this.resolveTopicGroupName(this.subscribeRelation.group());
        this.consumer.setConsumerGroup(group);
        this.consumer.subscribe(topic, this.subscribeRelation.tag());
        this.consumer.registerMessageListener(this);
        this.consumer.start();
    }

    @PreDestroy
    public void shutdown() {
        this.consumer.shutdown();
        logger.info("consumer shut down hook [{}]", this.getClass().getSimpleName());
    }

    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> messages, ConsumeConcurrentlyContext concurrentlyContext) {
        return this.consumeMessage(messages);
    }

    private ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> messages) {
        ContinuousStopwatch continuousStopwatch = new ContinuousStopwatch(Stopwatch.createUnstarted());
        MessageExt messageExt = messages.stream().findFirst().orElse(null);
        if (messageExt == null) {
            return Action.Commit.action();
        }

        return this.trance(() -> this.consumeMessage(continuousStopwatch, messageExt), messageExt.getMsgId());
    }

    private ConsumeConcurrentlyStatus consumeMessage(ContinuousStopwatch continuousStopwatch, MessageExt messageExt) {
        Message message = this.convertMessage(messageExt);
        String body = new String(message.getBody(), StandardCharsets.UTF_8);
        String consumerName = this.getClass().getSimpleName();

        T topic = JsonUtil.read(this.bindClazz, body);
        try {
            if (!this.accept(message, topic)) {
                logger.info("{} ignore rocket message: {}", consumerName, body);
                return Action.Commit.action();
            }
        } catch (Throwable e) {
            logger.error("{} accept rocket message exception: {} with {}", consumerName, e, continuousStopwatch.reset(), e);
            return Action.Reconsume.action();
        }

        logger.info("{} begin rocket message: {}", consumerName, body);
        try {
            Action result = this.consume(message, topic);
            if (logger.isDebugEnabled()) {
                logger.debug("{} {} rocket message with {}", consumerName, result, continuousStopwatch.reset());
            }
            if (Action.Reconsume.equals(result) && message.getReconsumeTimes() >= thresholdOfErrorNotify(topic, message)) {
                logger.error("{} rocket message {} {} reconsume times {}", consumerName, message.getTopic(), message.getMsgId(), message.getReconsumeTimes());
            }
            return result.action();
        } catch (Throwable e) {
            logger.error("{} rocket message exception: {}, with {}", consumerName, e.getMessage(), continuousStopwatch.reset(), e);
            return Action.Reconsume.action();
        }
    }

}
