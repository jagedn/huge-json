package com.example.huge;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Consumes;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Optional;

@Controller("/parse")
public class ReadController {

    static final Logger LOG = LoggerFactory.getLogger(ReadController.class);
    final JsonFactory factory;
    final ObjectMapper jacksonMapper;

    public ReadController(ObjectMapper jacksonMapper) {
        this.jacksonMapper = jacksonMapper;
        factory = new JsonFactory();
        factory.setCodec(jacksonMapper);
    }

    @Get("/sum/{path}")
    public Mono<Long> get(final String path) throws IOException {
        File file = new File(path);
        return sumAmounts(file);
    }

    @Get("/stream/{path}{?min}")
    public Flux<MyModel> stream(final String path, Optional<Integer>min) throws IOException {
        File file = new File(path);
        return parseFile(file)
                .filter(m->
                        min.filter(integer -> m.amount >= integer).isPresent());
    }

    private Mono<Long>sumAmounts(File file) throws IOException {
        return parseFile(file)
                .map(model->model.amount)
                .reduce(0L, Long::sum);
    }

    private Flux<MyModel> parseFile(File file) throws IOException {
        final FileInputStream inputStream = new FileInputStream(file);
        final JsonParser parser = factory.createParser(inputStream);

        if (parser.nextToken() != JsonToken.START_ARRAY) {
            LOG.error("Expected an array");
            return Flux.empty();
        }

        return Flux.<MyModel>generate(sink -> {
                    try {
                        var nextToken = parser.nextToken();
                        if (nextToken != JsonToken.START_OBJECT) {
                            parser.close();
                            sink.complete();
                            return;
                        }

                        var model = parser.readValueAs(MyModel.class);
                        sink.next(model);

                    } catch (Exception e) {
                        LOG.error("Error ", e);
                        sink.error(e);
                    }

                }).cache(1_000);
    }

}
