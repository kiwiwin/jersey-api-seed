package com.github.kiwiwin.mybatis;

import com.google.inject.Injector;
import org.apache.ibatis.reflection.factory.DefaultObjectFactory;

import javax.inject.Inject;
import java.util.List;

/**
 * Note: This ObjectFactory is unnecessary if QueryInterceptor is used
 */
public class ObjectFactory extends DefaultObjectFactory {
//    @Inject
//    ServiceLocator locator;

    @Inject
    Injector injector;

    @Override
    @SuppressWarnings(value = "unchecked")
    public <T> T create(Class<T> type, List<Class<?>> constructorArgTypes, List<Object> constructorArgs) {
        T object = super.create(type, constructorArgTypes, constructorArgs);
        injector.injectMembers(object);
        return object;
    }
}
