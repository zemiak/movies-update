package com.zemiak.movies;

import javax.inject.Inject;

import com.zemiak.movies.infuse.InfuseService;

import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;

@QuarkusMain
public class Application implements QuarkusApplication {
    @Inject
    InfuseService service;

    @Override
    public int run(String... args) throws Exception {
        service.process();
        return 0;
    }

    public static void main(String[] args) {
        Quarkus.run(Application.class, args);
    }
}
