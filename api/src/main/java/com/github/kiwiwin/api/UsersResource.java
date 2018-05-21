package com.github.kiwiwin.api;

import com.github.kiwiwin.core.User;
import com.github.kiwiwin.core.UserRepository;

import javax.ws.rs.Consumes;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Map;

@Path("users")
public class UsersResource {
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response register(Map<String, Object> request,
                             @Context UserRepository users) {
        User user = users.register(request);

        return Response.created(Routing.user(user)).build();
    }

    @Path("{id}")
    public UserResource user(@PathParam("id") String uuid,
                             @Context UserRepository users) {
        return new UserResource(users.uuid(uuid).orElseThrow(NotFoundException::new));
    }
}
