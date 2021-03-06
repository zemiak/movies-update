package com.zemiak.movies.movie;

import java.util.List;

import javax.json.JsonObject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient
@Path("movies")
@Consumes(MediaType.APPLICATION_JSON)
public interface MovieClient {
    @GET
    @Path("ui/paged")
    List<MovieUI> all(@QueryParam("page") int page, @QueryParam("pageSize") int pageSize);

    @GET
    @Path("{id}")
    Movie find(@PathParam("id") Long id);

    @GET
    @Path("count")
    JsonObject count();

    @POST
    @Path("filternew")
    List<String> findNewMovies(List<String> fileNames);

    @POST
    @Path("fetch")
    List<MovieUI> getMovieData(List<String> fileNames);

    @POST
    @Path("filename")
    @Consumes(MediaType.TEXT_PLAIN)
    Movie createFilename(String fileName);

    @GET
    @Path("ui/recent")
    List<MovieUI> getRecentlyAddedMovies();

    @GET
    @Path("ui/new")
    List<MovieUI> getNewReleases();
}
