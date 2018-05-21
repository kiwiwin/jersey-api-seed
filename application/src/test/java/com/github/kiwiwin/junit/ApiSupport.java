package com.github.kiwiwin.junit;

import com.github.kiwiwin.util.JacksonConfigurator;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.servlet.ServletRegistration;
import org.glassfish.grizzly.servlet.WebappContext;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.filter.LoggingFilter;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;
import org.glassfish.jersey.test.DeploymentContext;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.spi.TestContainerException;
import org.junit.After;
import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.URI;
import java.util.UUID;

import static javax.ws.rs.client.Entity.entity;

public class ApiSupport {
    @Inject
    ClientConfigurator clientConfigurator;

    @Inject
    @Named("server_uri")
    String serverUri;

    @Inject
    SetUp setUp;

    JerseyTest test;

    public static String uuid() {
        return UUID.randomUUID().toString().replace("-", "").toUpperCase();
    }

    public static String toString(Object object) {
        return object != null ? String.valueOf(object) : null;
    }

    @Before
    public void setUp() throws Exception {
        test = new JerseyTest() {

            private ResourceConfig application;

            @Override
            protected Application configure() {
                return getApplication();
            }

            @Override
            protected URI getBaseUri() {
                return URI.create(serverUri);
            }

            @Override
            protected void configureClient(ClientConfig config) {
                super.configureClient(config);
                config.register(JacksonConfigurator.class);
                config.register(MultiPartFeature.class);

                config.property(ClientProperties.SUPPRESS_HTTP_COMPLIANCE_VALIDATION, true);
                clientConfigurator.config(config);
            }

            @Override
            protected TestContainerFactory getTestContainerFactory() throws TestContainerException {
                return new TestContainerFactory(getApplication());
            }

            private ResourceConfig getApplication() {
                if (application == null) {
                    application = ResourceConfig.forApplicationClass(InjectorBasedRunner.ApiTestResourceConfig.class);
                }
                return application;
            }
        };
        test.setUp();
        setUp.before();
    }

    @After
    public void tearDown() throws Exception {
        test.tearDown();
    }

    protected WebTarget target(String uri) {
        return test.target(uri);
    }

    protected WebTarget target(URI uri) {
        return test.target(uri.getPath());
    }

    public Response post(String uri, Object json) {
        return target(uri).request()
                .post(entity(json, MediaType.APPLICATION_JSON_TYPE));
    }

    public Response put(String uri, Object json) {
        return target(uri).request()
                .put(entity(json, MediaType.APPLICATION_JSON_TYPE));
    }

    protected Response get(String uri) {
        return target(uri).request().get();
    }

    protected Response delete(String uri) {
        return target(uri).request().delete();
    }

    protected Response get(String uri, Object json) {
        return target(uri).request()
                .method("get", Entity.entity(json, MediaType.APPLICATION_JSON_TYPE));
    }

    public interface ClientConfigurator {
        void config(ClientConfig config);
    }

    public interface SetUp {
        void before() throws IOException;
    }

    public static class TestContainerFactory implements org.glassfish.jersey.test.spi.TestContainerFactory {
        private static final java.util.logging.Logger LOGGER = java.util.logging.Logger.getLogger(TestContainerFactory.class.getName());

        private ResourceConfig resourceConfig;

        public TestContainerFactory(ResourceConfig resourceConfig) {
            this.resourceConfig = resourceConfig;
        }

        @Override
        public org.glassfish.jersey.test.spi.TestContainer create(URI baseUri, DeploymentContext deploymentContext) {
            return new TestContainer(baseUri, resourceConfig);
        }

        private static class TestContainer implements org.glassfish.jersey.test.spi.TestContainer {
            private static final Logger logger = LoggerFactory.getLogger(TestContainer.class.getName());

            private final URI baseUri;

            private HttpServer server;

            private TestContainer(URI baseUri, ResourceConfig resourceConfig) {
                this.baseUri = baseUri;

                WebappContext context = new WebappContext("Webapp");

                ServletRegistration servlet = context.addServlet("Servlet", new ServletContainer(resourceConfig));
                servlet.addMapping("/*");

                server = GrizzlyHttpServerFactory.createHttpServer(baseUri);
                context.deploy(server);
            }

            @Override
            public ClientConfig getClientConfig() {
                ClientConfig clientConfig = new ClientConfig();
                return clientConfig.register(new LoggingFilter(LOGGER, true));
            }

            @Override
            public URI getBaseUri() {
                return baseUri;
            }

            @Override
            public void start() {
                logger.info("Starting JettyTestContainer...");
                try {
                    server.start();
                } catch (Exception e) {
                    throw new TestContainerException(e);
                }
            }

            @Override
            public void stop() {
                logger.info("Stopping TestContainer...");
                try {
                    this.server.shutdownNow();
                } catch (Exception ex) {
                    logger.info("Error Stopping TestContainer...", ex);
                }
            }
        }
    }

}
