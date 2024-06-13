package likeai.fun.consumer;


import java.util.Properties;
import java.util.function.Supplier;
import likeai.fun.mq.Message;
import likeai.fun.mq.RocketMqConfig;
import likeai.fun.topic.RocketTopic;
import org.apache.rocketmq.common.message.MessageExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

/**
 * @author chenaiquan
 * @date 2022/6/14 13:40
 */
public abstract class AbstractRocketConsumer<T extends RocketTopic> implements RocketConsumer<T> {
    protected static final Logger logger = LoggerFactory.getLogger(RocketConsumer.class);

    private final static String MID = "MID";
    protected final RocketMqConfig rocketMqConfig;

    public AbstractRocketConsumer(RocketMqConfig rocketMqConfig) {
        this.rocketMqConfig = rocketMqConfig.checkProperties();
    }

    protected Message convertMessage(MessageExt messageExt) {
        Message message = new Message();
        message.setBornTimestamp(messageExt.getBornTimestamp());
        message.setBornHost(messageExt.getBornHost());
        message.setMsgId(messageExt.getMsgId());
        message.setTopic(messageExt.getTopic());
        message.setBody(messageExt.getBody());
        message.setReconsumeTimes(messageExt.getReconsumeTimes());
        message.setTags(messageExt.getTags());
        return message;
    }

    protected <R> R trance(Supplier<R> call, String tid) {
        return this.trance(call, MID, tid);
    }

    protected <R> R trance(Supplier<R> call, String mdc, String tid) {
        MDC.put(mdc, tid);
        try {
            return call.get();
        } finally {
            MDC.remove(mdc);
        }
    }

    protected String resolveTopicGroupName(String topicName) {
        return this.rocketMqConfig.getEnv() + "-" + topicName;
    }

    protected int thresholdOfErrorNotify(T topic, Message message) {
        return 16;
    }

    /**
     * 自定义属性 但是要一个一个设置到 consumer 里
     */
    protected Properties prepareProperties() {
        Properties properties = new Properties();
        if (rocketMqConfig != null) {
            properties = rocketMqConfig.generateProperties();
        }
        final Properties consumerProperties = this.consumerProperties();
        if (consumerProperties != null) {
            for (String k : consumerProperties.stringPropertyNames()) {
                properties.setProperty(k, consumerProperties.getProperty(k));
            }
        }
        return properties;
    }

    /**
     * 自定义的 consumer 配置
     */
    protected Properties consumerProperties() {
        return null;
    }
}
