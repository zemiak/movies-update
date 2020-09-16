package com.zemiak.movies.infuse;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.zemiak.movies.genre.Genre;
import com.zemiak.movies.movie.MovieService;
import com.zemiak.movies.movie.MovieUI;
import com.zemiak.movies.movie.PrepareMovieFileList;

import org.eclipse.microprofile.config.inject.ConfigProperty;

@Dependent
public class InfuseMovieWriter {
    private static final Logger LOG = Logger.getLogger(InfuseMovieWriter.class.getName());

    @Inject
    @ConfigProperty(name = "media.path")
    String path;

    @Inject
    MovieService service;

    @Inject
    InfuseCoversAndLinks metadataFiles;

    @Inject
    PrepareMovieFileList movieList;

    public void process() {
        List<String> files = movieList.getFiles();
        int pageSize = 50;
        int pageCount = files.size() / pageSize;

        for (int i = 0 ; i < pageCount ; i++) {
            processPage(files.subList(i*pageSize, (i+1)*pageSize));
        }

        if (files.size() % pageSize != 0) {
            processPage(files.subList(pageCount*pageSize, files.size()));
        }

        makeRecentlyAdded();
        makeNewReleases();

        metadataFiles.createGenreAndSerieCovers();
    }

    private void processPage(List<String> files) {
        service.getMovieData(files).forEach(this::makeMovieLinkNoException);
    }

    private void makeMovieLinkNoException(MovieUI movie) {
        try {
            makeMovieLink(movie);
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "Cannot make movie link for " + movie.fileName + ": " + ex.getMessage());
        }
    }

    private void makeMovieLink(MovieUI movie) throws IOException {
        if (null == movie.genre) {
            LOG.log(Level.SEVERE, "Movie {0} has no genre", movie.fileName);
            return;
        }

        String movieName = getNumberPrefix(movie) +
                ((null == movie.originalName || "".equals(movie.originalName.trim()))
                ? movie.name : movie.originalName);
        if (null == movieName || "".equals(movieName)) {
            LOG.log(Level.SEVERE, "Movie {0} has no name", movie.fileName);
            return;
        }

        int i = 0;
        while (!metadataFiles.createLink(movie, movieName, i)) {
            i++;
        }

        LOG.log(Level.FINE, "Created Infuse movie link for movie ", movie.fileName);
    }

    private void makeRecentlyAdded() {
        service.getRecentlyAddedMovies().stream().forEach(movie -> {
            movie.genre = Genre.getRecentlyAddedGenre().name;
            movie.serie = "None";
            makeMovieLinkNoException(movie);
        });
    }

    private void makeNewReleases() {
        service.getNewReleases().stream().forEach(movie -> {
            movie.genre = Genre.getFreshGenre().name;
            movie.serie = "None";
            makeMovieLinkNoException(movie);
        });
    }

    private String getNumberPrefix(MovieUI movie) {
        if ((null == movie.serie || movie.serie.isBlank()) && Objects.nonNull(movie.year) && movie.year > 1800) {
            return String.format("%03d", (2500 - movie.year)) + "-";
        }

        if (null == movie.displayOrder || movie.displayOrder.equals(0) || movie.displayOrder > 999) {
            return "";
        }

        return String.valueOf(movie.displayOrder);
    }
}
