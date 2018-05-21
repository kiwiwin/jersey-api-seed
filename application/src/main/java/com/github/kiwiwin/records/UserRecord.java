package com.github.kiwiwin.records;

import com.github.kiwiwin.core.User;
import com.google.common.collect.ImmutableMap;
import org.joda.time.DateTime;

import java.util.Map;

public class UserRecord implements User, JsonType {
    protected String uuid;
    protected DateTime createdAt;
    protected String name;

    @Override
    public String uuid() {
        return uuid;
    }

    @Override
    public DateTime createdAt() {
        return createdAt;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public Map<String, Object> toJson() {
        return ImmutableMap.<String, Object>builder()
                .put("id", uuid)
                .put("created_at", createdAt)
                .put("name", name)
                .build();
    }
}
