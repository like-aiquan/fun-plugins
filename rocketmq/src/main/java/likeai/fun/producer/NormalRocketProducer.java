package likeai.fun.producer;


import likeai.fun.fallback.FallBackService;
import likeai.fun.mq.RocketMqConfig;
import likeai.fun.topic.RocketTopic;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;

/**
 * @author chenaiquan
 * @date 2022/6/14 14:08
 */
public class NormalRocketProducer extends AbstractRocketProducer {
    private final DefaultMQProducer producer;

    public NormalRocketProducer(RocketMqConfig config, FallBackService fallBackService) {
        this(config, fallBackService, new DefaultMQProducer());
    }

    public NormalRocketProducer(RocketMqConfig config, FallBackService fallBackService, DefaultMQProducer producer) {
        super(config, fallBackService);

        this.producer = producer;
        this.start();
    }

    private void start() {
        try {
            this.producer.setProducerGroup(this.config.getProducerGroup());
            this.producer.setNamespace(this.config.getNameSpace());
            String nameSrvAddr = this.config.getNameSrvAddr();
            this.producer.setNamesrvAddr(nameSrvAddr);
            this.producer.start();
            logger.info("start producer success {}", this.getClass().getSimpleName());
        } catch (MQClientException e) {
            logger.error("start producer error {} {}", this.getClass().getSimpleName(), e.getErrorMessage(), e);
        }
    }

    public void destroy() {
        this.producer.shutdown();
    }

    @Override
    protected SendResult send(Message message, RocketTopic topic) {
        try {
            return this.producer.send(message);
        } catch (MQClientException | RemotingException | MQBrokerException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
