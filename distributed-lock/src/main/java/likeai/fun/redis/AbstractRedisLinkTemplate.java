package likeai.fun.redis;

import static java.util.Objects.requireNonNull;

import io.lettuce.core.ClientOptions;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisConnectionStateListener;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.async.RedisAsyncCommands;
import io.lettuce.core.api.push.PushListener;
import io.lettuce.core.api.reactive.RedisReactiveCommands;
import io.lettuce.core.api.sync.RedisCommands;
import io.lettuce.core.codec.RedisCodec;
import io.lettuce.core.protocol.RedisCommand;
import io.lettuce.core.resource.ClientResources;
import java.time.Duration;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

/**
 * @author likeai
 */
public abstract class AbstractRedisLinkTemplate<K, V> implements StatefulRedisConnection<K, V> {
    protected final RedisClient client;
    protected StatefulRedisConnection<K, V> connect;

    public AbstractRedisLinkTemplate(RedisClient client, RedisCodec<K, V> codec) {
        requireNonNull(client, "require a redis client!");
        requireNonNull(codec, "require a redis codec!");
        this.client = client;
        this.connect = this.client.connect(codec);
    }

    @Override
    public boolean isMulti() {
        return this.connect.isMulti();
    }

    @Override
    public RedisCommands<K, V> sync() {
        return this.connect.sync();
    }

    @Override
    public RedisAsyncCommands<K, V> async() {
        return this.connect.async();
    }

    @Override
    public RedisReactiveCommands<K, V> reactive() {
        return this.connect.reactive();
    }

    @Override
    public void addListener(PushListener listener) {
        this.connect.addListener(listener);
    }

    @Override
    public void removeListener(PushListener listener) {
        this.connect.removeListener(listener);
    }

    @Override
    public void addListener(RedisConnectionStateListener listener) {
        this.connect.addListener(listener);
    }

    @Override
    public void removeListener(RedisConnectionStateListener listener) {
        this.connect.removeListener(listener);
    }

    @Override
    public void setTimeout(Duration timeout) {
        this.connect.setTimeout(timeout);
    }

    @Override
    public Duration getTimeout() {
        return this.connect.getTimeout();
    }

    @Override
    public <T> RedisCommand<K, V, T> dispatch(RedisCommand<K, V, T> command) {
        return this.connect.dispatch(command);
    }

    @Override
    public Collection<RedisCommand<K, V, ?>> dispatch(Collection<? extends RedisCommand<K, V, ?>> commands) {
        return this.connect.dispatch(commands);
    }

    @Override
    public void close() {
        this.connect.close();
    }

    @Override
    public CompletableFuture<Void> closeAsync() {
        return this.connect.closeAsync();
    }

    @Override
    public boolean isOpen() {
        return this.connect.isOpen();
    }

    @Override
    public ClientOptions getOptions() {
        return this.connect.getOptions();
    }

    @Override
    public ClientResources getResources() {
        return this.connect.getResources();
    }

    @Override
    @Deprecated
    public void reset() {
        this.connect.reset();
    }

    @Override
    public void setAutoFlushCommands(boolean autoFlush) {
        this.connect.setAutoFlushCommands(autoFlush);
    }

    @Override
    public void flushCommands() {
        this.connect.flushCommands();
    }
}
