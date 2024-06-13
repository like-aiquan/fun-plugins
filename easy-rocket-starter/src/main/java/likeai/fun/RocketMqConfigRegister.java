package likeai.fun;

import likeai.fun.mq.RocketMqConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author likeai
 */
@Configuration(proxyBeanMethods = false)
public class RocketMqConfigRegister {
    @Bean
    @ConfigurationProperties(prefix = "easy.rocketmq")
    public RocketMqProperties rocketMqProperties() {
        return new RocketMqProperties();
    }

    @Bean
    public RocketMqConfig properties(RocketMqProperties rocketMqProperties) {
        RocketMqConfig config = new RocketMqConfig();
        config.setEnv(rocketMqProperties.getEnv());
        config.setNameSrvAddr(rocketMqProperties.getNameSrvAddr());
        config.setNameSpace(rocketMqProperties.getNameSpace());
        config.setProducerGroup(rocketMqProperties.getProducerGroup());
        return config;
    }
}
