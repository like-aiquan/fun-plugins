package likeai.fun.json;

import static java.util.Objects.nonNull;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import java.io.IOException;
import java.time.Instant;

/**
 * Instant class serializer
 */
public class InstantSerializer extends StdSerializer<Instant> implements ContextualSerializer, Constants {
    private final String format;

    public InstantSerializer() {
        this(FORMAT_EPOCH_SECOND);
    }

    public InstantSerializer(String format) {
        super(Instant.class);
        this.format = format;
    }

    @Override
    public void serialize(Instant value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (FORMAT_EPOCH_MILLI.equals(this.format)) {
            gen.writeNumber(value.toEpochMilli());
        } else {
            gen.writeNumber(value.getEpochSecond());
        }
    }

    @Override
    public JsonSerializer<?> createContextual(SerializerProvider prov, BeanProperty property) {
        JsonFormat.Value format = findFormatOverrides(prov, property, handledType());
        if (nonNull(format) && format.hasPattern() && FORMAT_EPOCH_MILLI.equals(format.getPattern())) {
            return new InstantSerializer(FORMAT_EPOCH_MILLI);
        }
        return this;
    }
}