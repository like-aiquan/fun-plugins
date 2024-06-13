package likeai.fun.listner;

import likeai.fun.producer.NormalRocketProducer;
import likeai.fun.producer.OrderRocketProducer;
import likeai.fun.topic.NormalRocketTopic;
import likeai.fun.topic.OrderRocketTopic;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * @author lieai
 */
public class SendRockTransactionListener extends AbstractRocketMqEventListener {

    public SendRockTransactionListener(NormalRocketProducer normalRocketProducer, OrderRocketProducer orderRocketProducer) {
        super(normalRocketProducer, orderRocketProducer);
    }

    @TransactionalEventListener
    public void onApplicationEvent(NormalRocketTopic topic) {
        this.sendNormal(topic);
    }

    @TransactionalEventListener
    public void onApplicationEvent(OrderRocketTopic topic) {
        this.sendOrder(topic);
    }
}
