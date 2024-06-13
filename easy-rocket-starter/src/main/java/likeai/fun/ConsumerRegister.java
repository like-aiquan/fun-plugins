package likeai.fun;

import likeai.fun.consumer.RocketConsumer;
import org.apache.rocketmq.client.exception.MQClientException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;

/**
 * @author likeai
 */
@Configuration(proxyBeanMethods = false)
public class ConsumerRegister implements CommandLineRunner {
    private final static Logger log = LoggerFactory.getLogger(ConsumerRegister.class);
    private final ApplicationContext applicationContext;

    public ConsumerRegister(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public void run(String... args) {
        this.applicationContext.getBeansOfType(RocketConsumer.class)
                .values()
                .forEach(consumer -> {
                    try {
                        consumer.start();
                        log.info("start consumer success! {}", consumer.getClass().getSimpleName());
                    } catch (MQClientException e) {
                        log.error("start consumer error! {} {}", consumer.getClass().getSimpleName(), e.getErrorMessage(), e);
                    }
                });
    }
}
