package likeai.fun;

import likeai.fun.topic.NormalRocketTopic;

public class TestNormalTopic extends NormalRocketTopic {
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
