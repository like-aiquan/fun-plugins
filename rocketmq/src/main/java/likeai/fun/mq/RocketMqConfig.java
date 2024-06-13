package likeai.fun.mq;

import static likeai.fun.StringUtils.hasText;
import static likeai.fun.StringUtils.requireHasText;

import java.util.Properties;
import org.apache.rocketmq.common.utils.NameServerAddressUtils;
import org.apache.rocketmq.logging.org.slf4j.Logger;
import org.apache.rocketmq.logging.org.slf4j.LoggerFactory;

/**
 * @author chenaiquan
 * @date 2022/6/12 14:18
 */
public class RocketMqConfig {
    private static final Logger log = LoggerFactory.getLogger(RocketMqConfig.class);
    /**
     * 用来区分环境，区分 rocket mq 的 topic 和 group 的命名 规范的命名才是写好代码的第一步
     */
    private String env;
    /**
     * rocket mq name srv address 当然 也可以使用系统的环境变量设置
     */
    private String nameSrvAddr;
    /**
     * 系统全局的 name space 可以没有 name space 是 topic  group 的前缀，所有 topic group 都在当前名称空间下才能互相正常消费， 建议不用  感觉很鸡肋
     */
    private String nameSpace;
    /**
     * 默认 producer 组
     */
    private String producerGroup;

    public Properties generateProperties() {
        return new Properties();
    }

    public String getEnv() {
        return env;
    }

    public RocketMqConfig setEnv(String env) {
        this.env = env;
        return this;
    }

    public String getNameSrvAddr() {
        return nameSrvAddr;
    }

    public RocketMqConfig setNameSrvAddr(String nameSrvAddr) {
        this.nameSrvAddr = nameSrvAddr;
        return this;
    }

    public String getNameSpace() {
        return nameSpace;
    }

    public RocketMqConfig setNameSpace(String nameSpace) {
        this.nameSpace = nameSpace;
        return this;
    }

    public String getProducerGroup() {
        return producerGroup;
    }

    public RocketMqConfig setProducerGroup(String producerGroup) {
        this.producerGroup = producerGroup;
        return this;
    }

    public RocketMqConfig checkProperties() {
        requireHasText(this.getEnv());
        if (hasText(this.getProducerGroup())) {
            this.setProducerGroup(this.getEnv());
            log.warn("empty producer group, will use like env");
        }

        // fixme 是否可以？
        // fixme 不用校验 name srv  如果是 consumer 模块启动时会直接开始拉取的动作，会直接报错
        // fixme producer 模块会推送失败 可以使用或实现 fallbackService 记录错误
        String nameSrvAddr = this.getNameSrvAddr();
        if (hasText(nameSrvAddr)) {
            nameSrvAddr = NameServerAddressUtils.getNameServerAddresses();
            if (hasText(nameSrvAddr)) {
                throw new NullPointerException("check set name srv addr");
            }
            this.setNameSrvAddr(nameSrvAddr);
        }
        return this;
    }
}
