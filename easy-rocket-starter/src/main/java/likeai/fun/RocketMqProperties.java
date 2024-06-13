package likeai.fun;

/**
 * @author likeai
 */
public class RocketMqProperties {
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

    public String getEnv() {
        return env;
    }

    public void setEnv(String env) {
        this.env = env;
    }

    public String getNameSrvAddr() {
        return nameSrvAddr;
    }

    public void setNameSrvAddr(String nameSrvAddr) {
        this.nameSrvAddr = nameSrvAddr;
    }

    public String getNameSpace() {
        return nameSpace;
    }

    public void setNameSpace(String nameSpace) {
        this.nameSpace = nameSpace;
    }

    public String getProducerGroup() {
        return producerGroup;
    }

    public void setProducerGroup(String producerGroup) {
        this.producerGroup = producerGroup;
    }
}
