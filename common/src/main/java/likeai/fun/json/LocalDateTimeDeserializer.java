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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LocalDateTimeDeserializer extends StdDeserializer<LocalDateTime> implements ContextualDeserializer {
    private final DateTimeFormatter formatter;

    public LocalDateTimeDeserializer() {
        this(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    public LocalDateTimeDeserializer(DateTimeFormatter formatter) {
        super(LocalDateTime.class);
        this.formatter = formatter;
    }

    @Override
    public LocalDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        if (p.hasToken(JsonToken.VALUE_STRING)) {
            String text = p.getText().trim();
            if (text.isEmpty()) {
                return null;
            }
            return LocalDateTime.parse(text, formatter);
        }
        return (LocalDateTime) ctxt.handleUnexpectedToken(_valueClass, p);
    }

    @Override
    public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property) {
        JsonFormat.Value format = findFormatOverrides(ctxt, property, handledType());
        if (nonNull(format) && format.hasPattern()) {
            return new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern(format.getPattern()));
        }
        return this;
    }
}
