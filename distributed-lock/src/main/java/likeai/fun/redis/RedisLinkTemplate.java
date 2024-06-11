package likeai.fun.redis;

import io.lettuce.core.RedisClient;
import io.lettuce.core.codec.StringCodec;

/**
 * @author likeai
 */
public class RedisLinkTemplate extends AbstractRedisLinkTemplate<String, String> {

    public RedisLinkTemplate(RedisClient client) {
        super(client, StringCodec.UTF8);
    }
}
