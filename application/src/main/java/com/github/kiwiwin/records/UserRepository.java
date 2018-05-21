package com.github.kiwiwin.records;

import com.github.kiwiwin.core.User;
import com.github.kiwiwin.mybatis.mapper.UserMapper;

import javax.inject.Inject;
import java.util.Map;
import java.util.Optional;

public class UserRepository implements com.github.kiwiwin.core.UserRepository {
    @Inject
    UserMapper mapper;

    @Override
    public User register(Map<String, Object> request) {
        mapper.insert(request);
        return mapper.uuid((String) request.get("uuid"));
    }

    @Override
    public Optional<User> uuid(String uuid) {
        return Optional.ofNullable(mapper.uuid(uuid));
    }
}
