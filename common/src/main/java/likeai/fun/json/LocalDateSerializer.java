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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * @author likeai
 */
public class LocalDateSerializer extends StdSerializer<LocalDate> implements ContextualSerializer {
    private final DateTimeFormatter formatter;

    public LocalDateSerializer() {
        this(DateTimeFormatter.ISO_LOCAL_DATE);
    }

    public LocalDateSerializer(DateTimeFormatter formatter) {
        super(LocalDate.class);
        this.formatter = formatter;
    }

    @Override
    public void serialize(LocalDate value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeString(value.format(formatter));
    }

    @Override
    public JsonSerializer<?> createContextual(SerializerProvider prov, BeanProperty property) {
        JsonFormat.Value format = findFormatOverrides(prov, property, handledType());
        if (nonNull(format) && format.hasPattern()) {
            return new LocalDateSerializer(DateTimeFormatter.ofPattern(format.getPattern()));
        }
        return this;
    }
}

