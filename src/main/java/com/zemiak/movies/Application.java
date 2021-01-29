package com.zemiak.movies;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.zemiak.movies.infuse.InfuseService;
import com.zemiak.movies.movie.NewMoviesCreator;

import io.quarkus.scheduler.Scheduled;

@ApplicationScoped
public class Application {
    @Inject
    InfuseService infuse;

    @Inject
    NewMoviesCreator creator;

    @Scheduled(cron="0 15 3 * * ?") // 3:15am every day
    public void run() {
        creator.process();
        infuse.process();
    }
}
