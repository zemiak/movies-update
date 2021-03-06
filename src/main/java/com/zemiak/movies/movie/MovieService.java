package com.zemiak.movies.movie;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.function.Consumer;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.RestClientBuilder;

@Dependent
public class MovieService {
    MovieClient client;

    @Inject
    @ConfigProperty(name = "movies.url")
    String moviesUrl;

    @PostConstruct
    public void init() {
        URL apiUrl;
        try {
            apiUrl = new URL(moviesUrl);
        } catch (MalformedURLException e) {
            throw new RuntimeException("Malformed backend url " + moviesUrl, e);
        }

        this.client = RestClientBuilder.newBuilder()
            .baseUrl(apiUrl)
            .build(MovieClient.class);
    }

    public List<MovieUI> all(int page, int pageSize) {
        return client.all(page, pageSize);
    }

    public Movie find(Long id) {
        return client.find(id);
    }

    public Long count() {
        return Long.valueOf(client.count().getInt("count"));
    }

    public List<String> filterNewMovies(List<String> fileNames) {
        return client.findNewMovies(fileNames);
    }

    public Movie createFilename(String fileName) {
        return client.createFilename(fileName);
    }

    public void traverse(Consumer<MovieUI> action) {
        long count = count();
        int pageSize = 10;
        long pageCount = count / pageSize + (count % pageSize > 0 ? 1 : 0);
        int pageIndex = 0;
        while (pageIndex < pageCount) {
            all(pageIndex, pageSize).stream().map(e -> (MovieUI) e).forEach(action);
            pageIndex++;
        }
    }

    public static String removeFileSeparatorFromStartIfNeeded(String relative) {
        return !relative.startsWith(File.separator) ? relative : relative.substring(1);
    }

    public List<MovieUI> getMovieData(List<String> fileNames) {
        return client.getMovieData(fileNames);
    }

    public List<MovieUI> getRecentlyAddedMovies() {
        return client.getRecentlyAddedMovies();
    }

    public List<MovieUI> getNewReleases() {
        return client.getNewReleases();
    }
}
