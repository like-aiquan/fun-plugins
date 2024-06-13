package likeai.fun;

import likeai.fun.listner.SendRockTransactionListener;
import likeai.fun.listner.SendRocketListener;
import likeai.fun.producer.NormalRocketProducer;
import likeai.fun.producer.OrderRocketProducer;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * @author likeai
 */
@Configuration(proxyBeanMethods = false)
public class ProducerListenerRegister {

    @Bean(destroyMethod = "destroy")
    @ConditionalOnClass(TransactionalEventListener.class)
    public SendRockTransactionListener transactionalListener(ObjectProvider<NormalRocketProducer> normalMqProducer,
            ObjectProvider<OrderRocketProducer> orderMqProducer) {
        return new SendRockTransactionListener(normalMqProducer.getIfUnique(), orderMqProducer.getIfUnique());
    }

    @Bean(destroyMethod = "destroy")
    @ConditionalOnMissingBean(SendRockTransactionListener.class)
    public SendRocketListener missTransactionalListener(ObjectProvider<NormalRocketProducer> normalProducer,
            ObjectProvider<OrderRocketProducer> orderProducer) {
        return new SendRocketListener(normalProducer.getIfUnique(), orderProducer.getIfUnique());
    }
}
