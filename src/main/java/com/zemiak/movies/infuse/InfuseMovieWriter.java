package com.zemiak.movies.infuse;

import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.temporal.ChronoField;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.zemiak.movies.genre.Genre;
import com.zemiak.movies.movie.Movie;
import com.zemiak.movies.movie.MovieService;
import com.zemiak.movies.serie.Serie;

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

    public void process(final List<String> list) {
        list.stream()
                .map(fileName -> Paths.get(fileName).toFile().getAbsolutePath())
                .map(fileName -> service.findByFilename(fileName.substring(path.length())))
                .filter(movie -> null != movie)
                .forEach(this::makeMovieLinkNoException);

        makeRecentlyAdded();
        makeNewReleases();

        metadataFiles.createGenreAndSerieCovers();
    }

    private void makeMovieLinkNoException(Movie movie) {
        try {
            makeMovieLink(movie);
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "Cannot make movie link for " + movie.fileName + ": " + ex.getMessage());
        }
    }

    private void makeMovieLink(Movie movie) throws IOException {
        if (null == movie.genreId) {
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
        service.getRecentlyAdded().stream().forEach(movie -> {
            movie.genreId = Genre.ID_RECENTLY_ADDED;
            movie.serieId = Serie.ID_NONE;
            makeMovieLinkNoException(movie);
        });
    }

    private void makeNewReleases() {
        int year = LocalDateTime.now().get(ChronoField.YEAR);
        service.getNewReleases(year).stream().forEach(movie -> {
            movie.genreId = Genre.ID_FRESH;
            movie.serieId = Serie.ID_NONE;
            makeMovieLinkNoException(movie);
        });
    }

    private String getNumberPrefix(Movie movie) {
        if ((null == movie.serieId || 0 == movie.serieId) && Objects.nonNull(movie.year) && movie.year > 1800) {
            return String.format("%03d", (2500 - movie.year)) + "-";
        }

        if (null == movie.displayOrder || movie.displayOrder.equals(0) || movie.displayOrder > 999) {
            return "";
        }

        return String.valueOf(movie.displayOrder);
    }
}
