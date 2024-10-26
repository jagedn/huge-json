package com.example.huge;

import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.serde.ObjectMapper;
import jakarta.inject.Inject;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

@Controller("generate")
public class WriteController {

    @Inject
    ObjectMapper objectMapper;

    @Get("{path}{?max}")
    public int generate(String path, Optional<Integer>max){
        try {
            try (var out = new PrintWriter(new FileOutputStream(path))) {
                out.println("[");
                int count=0;
                while(count < max.orElse(10_000_000)) {
                    out.print(generateRandomMap(count));
                    out.println(",");
                    count++;
                }
                out.print(generateRandomMap(count));
                out.println("\n]");
                return count;
            }
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }


    String generateRandomMap(int amount) throws IOException {
        return objectMapper.writeValueAsString(
                Map.of(
                        "name", "letter a",
                        "amount", amount,
                        "now", Instant.now().toString(),
                        "categories",
                        List.of(
                                Map.of("aa", "subcategory a", "bb", Instant.now().plusSeconds(10).toString())
                        )));
    }
}
