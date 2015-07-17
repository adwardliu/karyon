package com.netflix.karyon.admin.internal;

import com.netflix.archaius.annotations.Configuration;
import com.netflix.archaius.annotations.DefaultValue;

@Configuration(prefix="karyon.server.admin")
public interface AdminServerConfig {
    @DefaultValue("null")
    String name();
    
    @DefaultValue("1")
    int threads();

    @DefaultValue("8077")
    int port();

    @DefaultValue("0")
    int backlog();

    @DefaultValue("0")
    int shutdownDelay();
}
