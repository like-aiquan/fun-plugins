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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class LocalDateDeserializer extends StdDeserializer<LocalDate> implements ContextualDeserializer {
    private final DateTimeFormatter formatter;

    public LocalDateDeserializer() {
        this(DateTimeFormatter.ISO_LOCAL_DATE);
    }

    public LocalDateDeserializer(DateTimeFormatter formatter) {
        super(LocalDate.class);
        this.formatter = formatter;
    }

    @Override
    public LocalDate deserialize(JsonParser p, DeserializationContext ctx) throws IOException {
        if (!p.hasToken(JsonToken.VALUE_STRING)) {
            String text = p.getText().trim();
            if (text.isEmpty()) {
                return null;
            }
            return LocalDate.parse(p.getText(), formatter);
        }
        // support [2020, 1, 1] format
        if (p.isExpectedStartArrayToken()) {
            JsonToken t = p.nextToken();
            if (t == JsonToken.END_ARRAY) {
                return null;
            }

            if (t == JsonToken.VALUE_NUMBER_INT) {
                int year = p.getIntValue();
                int month = p.nextIntValue(-1);
                int day = p.nextIntValue(-1);

                if (p.nextToken() != JsonToken.END_ARRAY) {
                    ctx.reportWrongTokenException(this, JsonToken.END_ARRAY, null);
                }

                return LocalDate.of(year, month, day);
            }

            ctx.reportInputMismatch(this, "Unexpected token (%s), expected VALUE_NUMBER_INT", t);
        }
        return (LocalDate) ctx.handleUnexpectedToken(_valueClass, p);
    }

    @Override
    public JsonDeserializer<?> createContextual(DeserializationContext ctx, BeanProperty property) {
        // @JsonFormat support
        JsonFormat.Value format = findFormatOverrides(ctx, property, handledType());
        if (nonNull(format) && format.hasPattern()) {
            return new LocalDateDeserializer(DateTimeFormatter.ofPattern(format.getPattern()));
        }
        return this;
    }
}
