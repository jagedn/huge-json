package com.example.huge;

import io.micronaut.context.event.ApplicationEventListener;
import io.micronaut.runtime.Micronaut;
import io.micronaut.runtime.event.ApplicationStartupEvent;
import io.micronaut.serde.ObjectMapper;
import jakarta.inject.Inject;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Application implements ApplicationEventListener<ApplicationStartupEvent> {

    public static final int MAX_ITEMS = 10_000_000;
    @Inject
    ObjectMapper objectMapper;

    public static void main(String[] args) {
        Micronaut.run(Application.class, args);
    }

    @Override
    public void onApplicationEvent(ApplicationStartupEvent event) {
        try {
            try (var out = new PrintWriter(new FileOutputStream("test.json"))) {
                out.println("[");
                for (int i = 0; i < MAX_ITEMS - 1; i++) {
                    out.print(generateRandomMap());
                    out.println(",");
                }
                out.print(generateRandomMap());
                out.println("\n]");
            }
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    Random rnd = new Random();

    String generateRandomMap() throws IOException {
        return objectMapper.writeValueAsString(
                Map.of(
                        "name", "letter a",
                        "amount", rnd.nextInt(),
                        "now", Instant.now().toString(),
                        "categories",
                        List.of(
                                Map.of("aa", "subcategory a", "bb", Instant.now().plusSeconds(10).toString())
                        )));
    }
}