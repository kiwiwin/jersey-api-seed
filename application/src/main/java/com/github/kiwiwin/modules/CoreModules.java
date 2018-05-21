package com.github.kiwiwin.modules;

import com.github.kiwiwin.mybatis.Models;
import com.google.inject.Module;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;

public class CoreModules {
    public static List<Module> modules() {
        return new ArrayList<>(asList(
                new Models("development"),
                new DomainModule()
        ));
    }
}
