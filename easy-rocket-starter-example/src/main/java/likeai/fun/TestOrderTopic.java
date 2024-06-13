package likeai.fun;

import static likeai.fun.StringUtils.requireHasText;

import likeai.fun.topic.OrderRocketTopic;

public class TestOrderTopic extends OrderRocketTopic {
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
