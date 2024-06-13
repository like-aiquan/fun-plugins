package likeai.fun;

import likeai.fun.fallback.FallBackService;
import likeai.fun.mq.RocketMqConfig;
import likeai.fun.producer.NormalRocketProducer;
import likeai.fun.producer.OrderRocketProducer;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

/**
 * @author likeai
 */
@ConditionalOnClass(DefaultMQProducer.class)
public class ProducerRegister {
    @Bean(destroyMethod = "shutdown")
    @ConditionalOnMissingBean(name = "normalMqProducer")
    public DefaultMQProducer normalMqProducer() {
        return new DefaultMQProducer();
    }

    @Bean(destroyMethod = "shutdown")
    @ConditionalOnMissingBean(name = "orderMqProducer")
    public DefaultMQProducer orderMqProducer() {
        return new DefaultMQProducer();
    }

    @Bean(destroyMethod = "destroy")
    public NormalRocketProducer normalProducer(RocketMqConfig config,
            ObjectProvider<FallBackService> fallBackServices,
            @Qualifier("normalMqProducer") DefaultMQProducer normalMqProducer
    ) {
        return new NormalRocketProducer(config, fallBackServices.getIfUnique(), normalMqProducer);
    }

    @Bean(destroyMethod = "destroy")
    public OrderRocketProducer orderProducer(RocketMqConfig config,
            ObjectProvider<FallBackService> fallBackServices,
            @Qualifier("orderMqProducer") DefaultMQProducer orderMqProducer
    ) {
        return new OrderRocketProducer(config, fallBackServices.getIfUnique(), orderMqProducer);
    }
}
