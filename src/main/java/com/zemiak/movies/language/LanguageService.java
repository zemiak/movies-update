package com.zemiak.languages.language;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.function.Consumer;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.RestClientBuilder;

@Dependent
public class LanguageService {
    LanguageClient client;

    @Inject
    @ConfigProperty(name = "movies.url")
    String moviesUrl;

    @PostConstruct
    public void init() {
        URL apiUrl;
        try {
            apiUrl = new URL(moviesUrl);
        } catch (MalformedURLException e) {
            throw new RuntimeException("Malformed backend url " + moviesUrl, e);
        }

        this.client = RestClientBuilder.newBuilder()
            .baseUrl(apiUrl)
            .build(LanguageClient.class);
    }

    public List<Language> all(int page, int pageSize) {
        return client.all(page, pageSize);
    }

    public Language find(Long id) {
        return client.find(id);
    }

    public Long count() {
        return Long.valueOf(client.count().getInt("count"));
    }

    public void traverse(Consumer<Language> action) {
        long count = count();
        int pageSize = 10;
        long pageCount = count / pageSize + (count % pageSize > 0 ? 1 : 0);
        int pageIndex = 0;
        while (pageIndex < pageCount) {
            all(pageIndex, pageSize).stream().map(e -> (Language) e).forEach(action);
            pageIndex++;
        }
    }
}
