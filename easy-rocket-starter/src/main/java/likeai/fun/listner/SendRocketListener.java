package likeai.fun.listner;


import likeai.fun.producer.NormalRocketProducer;
import likeai.fun.producer.OrderRocketProducer;
import likeai.fun.topic.NormalRocketTopic;
import likeai.fun.topic.OrderRocketTopic;
import org.springframework.context.event.EventListener;

/**
 * mq 推送
 */
public class SendRocketListener extends AbstractRocketMqEventListener {

    public SendRocketListener(NormalRocketProducer normalRocketProducer, OrderRocketProducer orderRocketProducer) {
        super(normalRocketProducer, orderRocketProducer);
    }

    @EventListener
    public void onApplicationEvent(NormalRocketTopic topic) {
        this.sendNormal(topic);
    }

    @EventListener
    public void onApplicationEvent(OrderRocketTopic topic) {
        this.sendOrder(topic);
    }
}
