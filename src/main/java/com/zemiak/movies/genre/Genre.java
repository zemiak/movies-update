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
}
