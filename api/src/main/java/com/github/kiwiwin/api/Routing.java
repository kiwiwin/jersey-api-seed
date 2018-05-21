package com.github.kiwiwin.api;

import com.github.kiwiwin.core.User;

import java.net.URI;

import static javax.ws.rs.core.UriBuilder.fromUri;

public class Routing {
    public static URI user(User user) {
        return template("/users/{id}", user.uuid());
    }

    private static URI template(String template, Object... parameters) {
        return fromUri(template).build(parameters, false);
    }
}
