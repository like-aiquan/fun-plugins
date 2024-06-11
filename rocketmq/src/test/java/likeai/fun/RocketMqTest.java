package likeai.fun;

import static likeai.fun.StringUtils.requireHasText;

import likeai.fun.consumer.AbstractNormalRocketConsumer;
import likeai.fun.consumer.AbstractOrderRocketConsumer;
import likeai.fun.fallback.impl.DefaultFallBackServiceImpl;
import likeai.fun.mq.Action;
import likeai.fun.mq.Message;
import likeai.fun.mq.RocketMqProperties;
import likeai.fun.mq.SubscribeRelation;
import likeai.fun.producer.NormalRocketProducer;
import likeai.fun.producer.OrderRocketProducer;
import likeai.fun.topic.NormalRocketTopic;
import likeai.fun.topic.OrderRocketTopic;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * @author likeai
 */
public class RocketMqTest {
    static RocketMqProperties properties;

    @BeforeAll
    static void runWith() {
        properties = new RocketMqProperties()
                .setEnv("dev")
                .setNameSrvAddr("10.251.61.42:9876");
    }

    @Test
    void sendOrder() {
        for (int i = 0; i < 10; i++) {
            Data data = new Data().setName("likeai").setMessage("Order Hello World " + i);
            TestOrderTopic topic = new TestOrderTopic();
            topic.setKey(data.getName());
            topic.setData(data);

            OrderRocketProducer producer = new OrderRocketProducer(properties, new DefaultFallBackServiceImpl());
            producer.send(topic);
        }
    }

    @Test
    void sendNormal() {
        Data data = new Data().setName("likeai").setMessage("Normal Hello World");
        TestNormalTopic topic = new TestNormalTopic();
        topic.setData(data);

        NormalRocketProducer producer = new NormalRocketProducer(properties, new DefaultFallBackServiceImpl());
        producer.send(topic);
    }

    @Test
    void testNormalConsumer() throws InterruptedException {
        new NormalConsumerTest(properties);
        // hold on
        Thread.sleep(10000);
    }

    @Test
    void testOrderConsumer() throws InterruptedException {
        new OrderConsumerTest(properties);
        // hold on
        Thread.sleep(10000);
    }

    // TODO test consumer message with 'tag'
}

class OrderConsumerTest extends AbstractOrderRocketConsumer<TestOrderTopic> {
    private final static String TOPIC = TestOrderTopic.TOPIC_NAME;
    private final static String GROUP = "po-send-notify-test-consumer";

    private final static SubscribeRelation SUBSCRIBE_RELATION
            = SubscribeRelation.Builder.newBuilder().topic(TOPIC).group(GROUP).build();

    public OrderConsumerTest(RocketMqProperties rocketMqProperties) {
        super(rocketMqProperties, SUBSCRIBE_RELATION, TestOrderTopic.class);
    }

    @Override
    public boolean accept(Message message, TestOrderTopic topic) {
        return true;
    }

    @Override
    public Action consume(Message message, TestOrderTopic topic) {
        logger.info("{}", topic);
        return Action.Commit;
    }
}

class NormalConsumerTest extends AbstractNormalRocketConsumer<TestNormalTopic> {
    private final static String TOPIC = TestNormalTopic.TOPIC_NAME;
    private final static String GROUP = "no-send-notify-test-consumer";

    private final static SubscribeRelation SUBSCRIBE_RELATION
            = SubscribeRelation.Builder.newBuilder().topic(TOPIC).group(GROUP).build();

    public NormalConsumerTest(RocketMqProperties rocketMq) {
        super(rocketMq, SUBSCRIBE_RELATION, TestNormalTopic.class);
    }

    @Override
    public boolean accept(Message message, TestNormalTopic topic) {
        return true;
    }

    @Override
    public Action consume(Message message, TestNormalTopic topic) {
        logger.info("{}", topic);
        return Action.Commit;
    }
}

class TestOrderTopic extends OrderRocketTopic {
    public static final String TOPIC_NAME = "po-send-notify-test";

    @Override
    public String topicName() {
        return TOPIC_NAME;
    }

    @Override
    public String key() {
        requireHasText(this.key);
        return this.key;
    }

    private String key;
    private Data data;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "TestOrderTopic{" +
                "key='" + key + '\'' +
                ", data=" + data +
                '}';
    }
}

class TestNormalTopic extends NormalRocketTopic {
    public static final String TOPIC_NAME = "no-send-notify-test";

    @Override
    public String topicName() {
        return TOPIC_NAME;
    }

    private Data data;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "TestNormalTopic{" +
                "data=" + data +
                '}';
    }
}

class Data {
    private String name;
    private String message;

    public String getName() {
        return name;
    }

    public Data setName(String name) {
        this.name = name;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public Data setMessage(String message) {
        this.message = message;
        return this;
    }

    @Override
    public String toString() {
        return "Data{" +
                "name='" + name + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}