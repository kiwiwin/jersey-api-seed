package com.github.kiwiwin.modules;

import com.github.kiwiwin.core.UserRepository;
import com.google.inject.AbstractModule;

public class DomainModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(UserRepository.class).to(com.github.kiwiwin.records.UserRepository.class);
    }
}
