package com.zemiak;

import java.util.Arrays;
import java.util.stream.Collectors;

import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;

@QuarkusMain
public class Application implements QuarkusApplication {

    @Override
    public int run(String... args) throws Exception {
        System.out.println("Arguments: " + Arrays.asList(args).stream().collect(Collectors.joining()) + " (" + args.length + ")");
        return 0;
    }

    public static void main(String ...args) {
        Quarkus.run(Application.class, args);
    }
}
