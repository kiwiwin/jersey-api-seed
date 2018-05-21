package com.github.kiwiwin.core;

import java.util.Map;
import java.util.Optional;

public interface UserRepository {
    User register(Map<String, Object> request);

    Optional<User> uuid(String uuid);
}
