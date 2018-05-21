package com.github.kiwiwin.modules;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.util.Modules;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;
import org.jvnet.hk2.guice.bridge.api.GuiceIntoHK2Bridge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

import static org.jvnet.hk2.guice.bridge.api.GuiceBridge.getGuiceBridge;

public class Api extends ResourceConfig {

    private static final Logger logger = LoggerFactory.getLogger(Api.class.getName());

    @Inject
    public Api(ServiceLocator locator) {
        try {
            property(ServerProperties.RESPONSE_SET_STATUS_OVER_SEND_ERROR, true);
            register(MultiPartFeature.class);
            register(JacksonFeature.class);
            packages("com.github.kiwiwin");
            final Injector injector = apiInjector(locator);

            bridge(locator, injector);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            e.printStackTrace();
        }
    }

    private Injector apiInjector(ServiceLocator locator) {
        List<Module> modules = CoreModules.modules();
        modules.add(new AbstractModule() {

            @Override
            protected void configure() {
                bind(ServiceLocator.class).toInstance(locator);
            }
        });

        return Guice.createInjector(
                Modules.override(
                        modules
                ).with(overrideModules())
        );
    }

    protected List<Module> overrideModules() {
        return new ArrayList<>();
    }

    private void bridge(ServiceLocator serviceLocator, Injector injector) {
        getGuiceBridge().initializeGuiceBridge(serviceLocator);
        serviceLocator.getService(GuiceIntoHK2Bridge.class).bridgeGuiceInjector(injector);
    }
}
