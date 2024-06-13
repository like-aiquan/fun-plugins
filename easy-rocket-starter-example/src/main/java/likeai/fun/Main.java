package likeai.fun;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
@EnableEasyRocket(consumer = true, producer = true)
public class Main {

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(Main.class, args);

        // 分区顺序消息和无序消息同送和消费方式一致
        NoTestTopicProducer producer = context.getBean(NoTestTopicProducer.class);
        producer.send();
        // see likeai.rocketmq.NoTopicTestConsumer
    }
}