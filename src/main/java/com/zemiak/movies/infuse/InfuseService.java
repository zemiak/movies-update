package com.zemiak.movies.infuse;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

@Dependent
public class InfuseService {
    @Inject
    BasicInfuseFolderStructureCreator basic;

    @Inject
    InfuseMovieWriter writer;

    public void process() {
        basic.cleanAndCreate();
        writer.process();
    }
}
