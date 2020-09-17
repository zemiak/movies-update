package com.zemiak.movies.movie;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;

@Dependent
public class NewMoviesCreator {
    @Inject
    @ConfigProperty(name = "media.path")
    String path;

    @Inject
    MovieService service;

    @Inject
    PrepareMovieFileList movieList;

    private static final Logger LOG = Logger.getLogger(NewMoviesCreator.class.getName());

    public void process() {
        List<String> files = movieList.getFiles();
        int pageSize = 50;
        int pageCount = files.size() / pageSize;

        for (int i = 0 ; i < pageCount ; i++) {
            processPage(files.subList(i*pageSize, (i+1)*pageSize));
            System.out.println("... processed page " + i);
        }

        if (files.size() % pageSize != 0) {
            processPage(files.subList(pageCount*pageSize, files.size()));
            System.out.println("... processed last page");
        }
    }

    private void processPage(List<String> files) {
        List<String> newFiles = service.filterNewMovies(files);

        newFiles.stream()
                .map(this::getRelativeFilename)
                .map(fileName -> service.createFilename(fileName))
                .forEach(m -> {
                    LOG.log(Level.INFO, "Created a new movie ''{0}''/''{1}'', id {2}...",
                            new Object[]{m.fileName, m.name, m.id});
                });
    }

    private String getRelativeFilename(final String absoluteFilename) {
        String relative = absoluteFilename;
        if (relative.startsWith(path)) {
            relative = relative.substring(path.length());
        }

        relative = MovieService.removeFileSeparatorFromStartIfNeeded(relative);

        return relative;
    }
}
