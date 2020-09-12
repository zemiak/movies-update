package com.zemiak.movies.serie;

import java.time.LocalDateTime;

public class Serie {
    public static final Long ID_NONE = 0l;

    public Long id;
    public String name;
    public String pictureFileName;
    public Integer displayOrder;
    public Long genreId;
    public LocalDateTime created;
    public Boolean tvShow;
}
