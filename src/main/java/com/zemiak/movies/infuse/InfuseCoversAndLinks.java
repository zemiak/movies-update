package com.zemiak.movies.infuse;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.zemiak.movies.genre.Genre;
import com.zemiak.movies.movie.MovieService;
import com.zemiak.movies.movie.MovieUI;
import com.zemiak.movies.strings.Encodings;

import org.eclipse.microprofile.config.inject.ConfigProperty;

@Dependent
public class InfuseCoversAndLinks {
    private static final Logger LOG = Logger.getLogger(InfuseCoversAndLinks.class.getName());

    @Inject
    @ConfigProperty(name = "infuse.path")
    String infuseLinkPath;

    @Inject
    @ConfigProperty(name = "media.path")
    String path;

    @Inject
    @ConfigProperty(name = "image.path")
    String imgPath;

    @Inject
    MovieService service;

    @Inject
    InfuseMetadataWriter metadata;

    public void createGenreAndSerieCovers() {
        service.traverse(movie -> {
            if (null != movie.genre) {
                createGenreCover(movie);
            }

            if (null != movie.serie) {
                createSerieCover(movie);
            }
        });

        MovieUI movie = new MovieUI();

        movie.genrePictureFileName = Genre.getUnassignedGenre().pictureFileName;
        movie.genre = Genre.getUnassignedGenre().name;
        createGenreCover(movie);

        movie.genrePictureFileName = Genre.getFreshGenre().pictureFileName;
        movie.genre = Genre.getFreshGenre().name;
        createGenreCover(movie);

        movie.genrePictureFileName = Genre.getRecentlyAddedGenre().pictureFileName;
        movie.genre = Genre.getRecentlyAddedGenre().name;
        createGenreCover(movie);
    }

    public boolean createLink(MovieUI movie, String movieName, int order) throws IOException {
        String discriminator = 0 == order ? "" : "_" + order;
        Path linkName;

        if (null == movie.serie || movie.serie.isBlank() || movie.genre.equalsIgnoreCase("none")) {
            Files.createDirectories(Paths.get(infuseLinkPath,
                    Encodings.deAccent(getGenreName(movie))
            ));

            linkName = Paths.get(infuseLinkPath,
                    Encodings.deAccent(getGenreName(movie)),
                    Encodings.deAccent(movieName) + discriminator + ".m4v");
        } else {
            Files.createDirectories(Paths.get(infuseLinkPath,
                    Encodings.deAccent(getGenreName(movie)),
                    Encodings.deAccent(movie.serie)
            ));

            String movieNameInSerie = getSeriedMovieName(movie, movieName, discriminator);
            linkName = Paths.get(infuseLinkPath,
                    Encodings.deAccent(getGenreName(movie)),
                    Encodings.deAccent(movie.serie),
                    movieNameInSerie);
        }

        if (null == movie.fileName) {
            throw new RuntimeException("fileName of movie " + movie.name + " is null!");
        }

        Path existing = Paths.get(path, movie.fileName);

        try {
            Files.createSymbolicLink(linkName, existing);
        } catch (IOException ex) {
            return false;
        }

        createMovieCover(movie, linkName);
        metadata.createMetadataFile(movie, movieName, linkName);

        return true;
    }

    private void createSerieCover(MovieUI movie) {
        Path link = Paths.get(infuseLinkPath,
                    Encodings.deAccent(getGenreName(movie)),
                    Encodings.deAccent(movie.serie),
                    "folder." + getFileExt(movie.seriePictureFileName));
        Path existing = Paths.get(imgPath, "serie", movie.seriePictureFileName);

        createSymbolicLink(link, existing);
    }

    private void createGenreCover(MovieUI movie) {
        Path link = Paths.get(infuseLinkPath,
                    Encodings.deAccent(getGenreName(movie)),
                    "folder." + getFileExt(movie.genrePictureFileName));
        Path existing = Paths.get(imgPath, "genre", movie.genrePictureFileName);

        createSymbolicLink(link, existing);
    }

    private String getFileExt(String name) {
        int pos = name.lastIndexOf(".");
        return name.substring(pos + 1);
    }

    private void createMovieCover(MovieUI movie, Path linkName) {
        String linkAbsoluteName = linkName.toString();
        int pos = linkAbsoluteName.lastIndexOf("/");
        String fileNameWithExt = linkAbsoluteName.substring(pos + 1);
        String filePath = linkAbsoluteName.substring(0, pos);
        pos = fileNameWithExt.lastIndexOf(".");
        String fileNameWithoutExt = fileNameWithExt.substring(0, pos);

        Path link = Paths.get(filePath, fileNameWithoutExt + "." + getFileExt(movie.moviePictureFileName));
        Path existing = Paths.get(imgPath, "movie", movie.moviePictureFileName);
        createSymbolicLink(link, existing);
    }

    public static String getGenreName(MovieUI movie) {
        String name = movie.genre;
        if ("children".equalsIgnoreCase(name)) {
            name = "0-Children";
        } else if ("fresh".equalsIgnoreCase(name)) {
            name = "x-Fresh";
        } else if ("new".equalsIgnoreCase(name)) {
            name = "x-New";
        } else if ("none".equalsIgnoreCase(name)) {
            name = "x-None";
        }

        return name;
    }

    private String getSeriedMovieName(MovieUI movie, String movieName, String discriminator) {
        // Infuse somehow does not group series together :-(
        return getNiceDisplayOrder(movie) + " " + Encodings.deAccent(movieName) + discriminator
                + ".m4v";
    }

    private void createSymbolicLink(Path link, Path existing) {
        if (!link.toFile().exists()) {
            try {
                Files.createSymbolicLink(link, existing);
                LOG.log(Level.INFO, "Created link " + link.toString() + " --> " + existing.toString());
            } catch (IOException ex) {
                LOG.log(Level.INFO, "Cannot create symbolic link for {0} as {1}", new Object[]{link.toString(), existing.toString()});
            }
        }
    }

    private String getNiceDisplayOrder(MovieUI movie) {
        return String.format("%03d", movie.displayOrder);
    }
}
