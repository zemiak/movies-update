package com.zemiak.movies.genre;

import java.time.LocalDateTime;

public class Genre {
    public static final Long ID_NONE = 0L;
    public static final Long ID_FRESH = -1L;
    public static final Long ID_UNASSIGNED = -2L;
    public static final Long ID_RECENTLY_ADDED = -3L;

    public Long id;
    public String name;
    public Long protectedGenre;
    public String pictureFileName;
    public Long displayOrder;
    public LocalDateTime created;

    public static Genre getFreshGenre() {
        Genre g = new Genre();
        g.id = Genre.ID_FRESH;
        g.name = "Fresh";
        g.pictureFileName = "notdefined.png";
        return g;
    }

    public static Genre getUnassignedGenre() {
        Genre g = new Genre();
        g.id = Genre.ID_UNASSIGNED;
        g.name = "Unassigned";
        g.pictureFileName = "notdefined.png";
        return g;
    }

    public static Genre getRecentlyAddedGenre() {
        Genre g = new Genre();
        g.id = Genre.ID_RECENTLY_ADDED;
        g.name = "New";
        g.pictureFileName = "notdefined.png";
        return g;
    }

    public static boolean isArtificial(Long id) {
        return ID_FRESH.equals(id) || ID_RECENTLY_ADDED.equals(id) || ID_UNASSIGNED.equals(id);
    }
}
