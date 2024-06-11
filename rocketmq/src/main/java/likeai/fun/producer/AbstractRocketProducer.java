package likeai.fun.producer;


import com.google.common.base.Stopwatch;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.function.BiFunction;
import likeai.fun.ContinuousStopwatch;
import likeai.fun.fallback.FallBackService;
import likeai.fun.json.JsonUtil;
import likeai.fun.mq.RocketMqProperties;
import likeai.fun.topic.RocketTopic;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author chenaiquan
 * @date 2022/6/12 14:13
 */
public abstract class AbstractRocketProducer {
    protected static final Logger logger = LoggerFactory.getLogger(AbstractRocketProducer.class);
    protected RocketMqProperties producerProperties;
    protected FallBackService fallBackService;

    public AbstractRocketProducer(RocketMqProperties producerProperties, FallBackService fallBackService) {
        this.producerProperties = producerProperties.checkProperties();
        this.fallBackService = fallBackService;
    }

    public void send(RocketTopic topic) {
        ContinuousStopwatch continuousStopwatch = new ContinuousStopwatch(Stopwatch.createUnstarted());
        try {
            SendResult send = this.send(topic, this::send);
            logger.info("send success msg[{}] topic[{}] key[{}] tag[{}] {} with {}",
                    send.getMsgId(),
                    topic.topicName(),
                    topic.key(),
                    topic.tags(),
                    logBody(topic.logBody(), JsonUtil.write(topic)),
                    continuousStopwatch.reset());
        } catch (Throwable e) {
            if (this.fallBackService != null) {
                this.fallBackService.fallBack(topic, e);
            }
            logger.error("send failed topic[{}] key[{}] tag[{}] body[{}] {}", topic.topicName(), topic.key(), topic.tags(),
                    logBody(topic.logBody(), topic.toString()), e.getMessage(), e);
            throw e;
        }
    }

    protected abstract SendResult send(Message message, RocketTopic topic);

    protected SendResult send(RocketTopic topic, BiFunction<Message, RocketTopic, SendResult> sender) {
        String body = JsonUtil.write(topic);
        String topicName = this.resolveTopicName(topic.topicName());
        String tag = this.resolveTag(topic.tags());
        Message message = new Message(topicName, tag, body.getBytes(StandardCharsets.UTF_8));
        message.setDelayTimeLevel(topic.delayTimeLevel());
        return sender.apply(message, topic);
    }

    protected String logBody(boolean logBody, String body) {
        return logBody ? body : "...";
    }

    protected String resolveTag(List<String> tags) {
        return String.join("||", tags);
    }

    protected String resolveTopicName(String topicName) {
        return this.producerProperties.getEnv() + "-" + topicName;
    }
}
