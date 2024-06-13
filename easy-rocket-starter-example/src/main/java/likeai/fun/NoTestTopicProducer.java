package likeai.fun;

import java.util.stream.IntStream;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

/**
 * @author likeai
 */
@Service
public class NoTestTopicProducer implements ApplicationContextAware {
    private ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationEventPublisher = applicationContext;
    }

    public void send() {
        IntStream.range(0, 10)
                .mapToObj(i -> {
                    TestNormalTopic topic = new TestNormalTopic();
                    Data data = new Data();
                    data.setName("like--" + i);
                    topic.setData(data);
                    return topic;
                })
                .forEach(this.applicationEventPublisher::publishEvent);
    }
}
