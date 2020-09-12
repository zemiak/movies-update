package com.zemiak.movies.serie;

import java.util.List;

import javax.json.JsonObject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient
@Path("series")
@Consumes(MediaType.APPLICATION_JSON)
public interface SerieClient {
    @GET
    @Path("paged")
    List<Serie> all(@QueryParam("page") int page, @QueryParam("pageSize") int pageSize);

    @GET
    @Path("{id}")
    Serie find(@PathParam("id") Long id);

    @GET
    @Path("count")
    JsonObject count();
}
