package likeai.fun.json;

import static java.util.Objects.nonNull;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import java.io.IOException;
import java.time.Instant;

public class InstantDeserializer extends StdDeserializer<Instant> implements ContextualDeserializer, Constants {

    private final String format;

    public InstantDeserializer() {
        this(FORMAT_EPOCH_SECOND);
    }

    public InstantDeserializer(String format) {
        super(Instant.class);
        this.format = format;
    }

    @Override
    public Instant deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        if (p.hasToken(JsonToken.VALUE_NUMBER_INT)) {
            if (FORMAT_EPOCH_MILLI.equals(format)) {
                return Instant.ofEpochMilli(p.getLongValue());
            } else {
                return Instant.ofEpochSecond(p.getLongValue());
            }
        }
        if (p.hasToken(JsonToken.VALUE_NUMBER_FLOAT)) {
            if (FORMAT_EPOCH_MILLI.equals(format)) {
                // 忽略小数部分
                return extractSecondsAndNanos(p.getDecimalValue(), (s, n) -> Instant.ofEpochMilli(s));
            } else {
                return extractSecondsAndNanos(p.getDecimalValue(), Instant::ofEpochSecond);
            }
        }
        if (p.hasToken(JsonToken.VALUE_STRING)) {
            String text = p.getText().trim();
            if (text.isEmpty()) {
                return null;
            }
        }

        return (Instant) ctxt.handleUnexpectedToken(_valueClass, p);
    }

    @Override
    public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property) {
        JsonFormat.Value format = findFormatOverrides(ctxt, property, handledType());

        if (nonNull(format) && format.hasPattern() && FORMAT_EPOCH_MILLI.equals(format.getPattern())) {
            return new InstantDeserializer(FORMAT_EPOCH_MILLI);
        }

        return this;
    }
}
