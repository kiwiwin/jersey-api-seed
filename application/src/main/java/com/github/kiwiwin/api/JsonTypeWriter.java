package com.github.kiwiwin.api;

import com.github.kiwiwin.records.JsonType;
import com.github.kiwiwin.util.Json;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

@Provider
public class JsonTypeWriter implements MessageBodyWriter<JsonType> {
    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return JsonType.class.isAssignableFrom(type);
    }

    @Override
    public long getSize(JsonType jsonType, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return 0;
    }

    @Override
    public void writeTo(JsonType jsonType, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException {
        try (OutputStreamWriter outputStreamWriter = new OutputStreamWriter(entityStream, "UTF-8")) {
            outputStreamWriter.write(Json.toJson(jsonType.toJson()));
        }
    }
}
