package com.github.kiwiwin.api;

import com.github.kiwiwin.records.JsonType;
import com.github.kiwiwin.util.Json;
import com.sun.org.apache.xml.internal.security.encryption.ReferenceList;
import org.apache.commons.io.IOUtils;

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
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static java.util.stream.Collectors.toList;

@Provider
public class ListWriter implements MessageBodyWriter<List> {
    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return List.class.isAssignableFrom(type);
    }

    @Override
    public long getSize(List list, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return 0;
    }

    @Override
    public void writeTo(List list, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException {
        Function<JsonType, Map<String, Object>> toJson =
                list instanceof ReferenceList ? JsonType::toReferenceJson : JsonType::toJson;

        try (OutputStreamWriter writer = new OutputStreamWriter(entityStream, "UTF-8")) {
            IOUtils.write(Json.toJson(list.stream().map(toJson).collect(toList())), writer);
        }
    }
}
