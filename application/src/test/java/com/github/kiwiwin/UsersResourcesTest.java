package com.github.kiwiwin;

import com.github.kiwiwin.junit.ApiSupport;
import com.github.kiwiwin.junit.ApiTestRunner;
import com.github.kiwiwin.junit.JsonContext;
import com.google.common.collect.ImmutableMap;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.core.Response;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@RunWith(ApiTestRunner.class)
public class UsersResourcesTest extends ApiSupport {
    @Test
    public void should_register_user() throws Exception {
        Response createdResponse = post("/users", ImmutableMap.<String, Object>builder()
                .put("name", "kiwiwin")
                .build());

        assertThat(createdResponse.getStatus(), is(201));

        Response response = get("/users/" + getLocationCreatedId(createdResponse));
        assertThat(response.getStatus(), is(200));

        JsonContext json = JsonContext.json(response);

        assertThat(json.path("name"), is("kiwiwin"));
    }

    private String getLocationCreatedId(Response response) {
        String path = response.getLocation().getPath();
        String[] split = path.split("/");
        return split[split.length - 1];
    }
}
