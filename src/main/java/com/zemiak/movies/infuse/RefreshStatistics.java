package com.zemiak.movies.infuse;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class RefreshStatistics {
    private Logger LOG = Logger.getLogger(RefreshStatistics.class.getName());

    private int created;

    @PostConstruct
    public void reset() {
        created = 0;
    }

    public void dump() {
        LOG.log(Level.INFO, "Metadata Refresh Statistics");
        LOG.log(Level.INFO, "Movies Created: {0}", created);
    }

    public void incrementCreated() {
        created++;
    }

    boolean haveBeenChanged() {
        return created > 0;
    }
}
