package com.github.kiwiwin.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.fasterxml.jackson.datatype.joda.PackageVersion;
import org.joda.time.DateTime;

import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;

public class Json {
    public static final ObjectMapper mapper = new ObjectMapper();

    public static String toJson(Object value) {
        try {
            return mapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T fromJson(Blob blob, TypeReference<T> type) {
        try {
            return mapper.readValue(blob.getBinaryStream(), type);
        } catch (IOException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T fromJson(String blob, TypeReference<T> type) {
        try {
            return mapper.readValue(blob, type);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static class JodaTimeModule extends SimpleModule {
        public JodaTimeModule() {
            super(PackageVersion.VERSION);
            addSerializer(DateTime.class, new JsonSerializer<DateTime>() {

                @Override
                public void serialize(DateTime value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
                    jgen.writeNumber(value.getMillis());
                }
            });

            addDeserializer(DateTime.class, new JsonDeserializer<DateTime>() {

                @Override
                public DateTime deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
                    String value = jp.readValueAs(String.class);
                    return new DateTime(Long.parseLong(String.valueOf(value)));
                }
            });
        }
    }

    static {
        mapper.registerModule(new JodaModule());
        mapper.registerModule(new JodaTimeModule());
        mapper.configure(com.fasterxml.jackson.databind.SerializationFeature.
                WRITE_DATES_AS_TIMESTAMPS, false);
    }
}
