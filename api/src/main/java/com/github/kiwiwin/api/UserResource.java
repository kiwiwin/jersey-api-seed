package com.github.kiwiwin.api;

import com.github.kiwiwin.core.User;

import javax.ws.rs.GET;

public class UserResource {
    private final User user;

    public UserResource(User user) {
        this.user = user;
    }

    @GET
    public User get() {
        return user;
    }
}
