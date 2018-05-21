package com.github.kiwiwin.core;

import org.joda.time.DateTime;

public interface User {
    String uuid();

    DateTime createdAt();

    String name();
}
