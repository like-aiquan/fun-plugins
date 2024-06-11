package likeai.fun.json;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.module.SimpleModule;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author chenaiquan
 * @date 2022/6/14 17:05
 */
public final class JsonUtil {
    private static final ObjectMapper DEFAULT_MAPPER;
    private static final ObjectReader DEFAULT_READER;
    private static final ObjectWriter DEFAULT_WRITER;

    static {
        DEFAULT_MAPPER = new ObjectMapper();
        DEFAULT_MAPPER.enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL);
        DEFAULT_MAPPER.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        SimpleModule module = new SimpleModule()
                .addSerializer(LocalDate.class, new LocalDateSerializer())
                .addDeserializer(LocalDate.class, new LocalDateDeserializer())
                .addDeserializer(Instant.class, new InstantDeserializer())
                .addSerializer(Instant.class, new InstantSerializer())
                .addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer())
                .addSerializer(LocalDateTime.class, new LocalDateTimeSerializer());

        DEFAULT_MAPPER.registerModule(module);
        DEFAULT_READER = DEFAULT_MAPPER.reader();
        DEFAULT_WRITER = DEFAULT_MAPPER.writer();
    }

    public static ObjectMapper copy() {
        return DEFAULT_MAPPER.copy();
    }

    public static ObjectReader reader() {
        return JsonUtil.DEFAULT_READER;
    }

    public static ObjectWriter writer() {
        return JsonUtil.DEFAULT_WRITER;
    }

    public static <T> String write(T o) {
        try {
            return writer().writeValueAsString(o);
        } catch (JsonProcessingException e) {
            throw new UnsupportedOperationException(e);
        }
    }

    public static <T> T read(Class<T> clazz, String json) {
        try {
            return reader().forType(clazz).readValue(json);
        } catch (JsonProcessingException e) {
            throw new UnsupportedOperationException(e);
        }
    }
}
