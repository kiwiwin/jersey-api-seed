package com.github.kiwiwin.junit;

import com.github.kiwiwin.modules.Api;
import com.github.kiwiwin.mybatis.Models;
import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.name.Names;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.jersey.apache.connector.ApacheConnectorProvider;
import org.glassfish.jersey.internal.inject.Injections;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;
import org.jvnet.hk2.guice.bridge.api.GuiceIntoHK2Bridge;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

import static com.google.inject.Guice.createInjector;
import static java.util.Arrays.asList;
import static org.jvnet.hk2.guice.bridge.api.GuiceBridge.getGuiceBridge;

public class InjectorBasedRunner extends BlockJUnit4ClassRunner {
    private static final String SERVER_URI = "http://localhost:8888";

    protected static ServiceLocator locator = Injections.createLocator();

    public InjectorBasedRunner(Class<?> klass) throws InitializationError {
        super(klass);

        List<AbstractModule> modules = getAbstractModules();
        try {
            Injector injector = createInjector(modules);
            bridge(locator, injector);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        locator.inject(this);
    }

    private static void bridge(ServiceLocator serviceLocator, Injector injector) {
        getGuiceBridge().initializeGuiceBridge(serviceLocator);
        serviceLocator.getService(GuiceIntoHK2Bridge.class).bridgeGuiceInjector(injector);
    }

    private List<AbstractModule> getAbstractModules() {
        return new ArrayList<>(asList(new AbstractModule[]{
                new Models("development"),
                new AbstractModule() {

                    @Override
                    protected void configure() {
                        bindConstant().annotatedWith(Names.named("server_uri")).to(SERVER_URI);
                        bind(ServiceLocator.class).toInstance(locator);

                        bind(ApiSupport.ClientConfigurator.class).toInstance(config -> {
                            config.register(JacksonFeature.class);
                            config.connectorProvider(new ApacheConnectorProvider());
                        });

                        bind(ApiSupport.SetUp.class).toInstance(() -> {
                        });
                    }
                }}));
    }

    @Override
    protected Object createTest() throws Exception {
        Object testClass = super.createTest();
        locator.inject(testClass);
        return testClass;
    }

    public static class ApiTestResourceConfig extends Api {
        @Inject
        public ApiTestResourceConfig(ServiceLocator locator) throws Exception {
            super(locator);
        }

        @Override
        protected List<Module> overrideModules() {
            return asList(new AbstractModule() {

                @Override
                protected void configure() {
                }
            });
        }
    }

    private static class TestModule extends AbstractModule {
        @Override
        protected void configure() {
        }
    }
}
