package com.example.huge;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Consumes;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.multipart.StreamingFileUpload;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@Controller("/parse")
public class ReadController {

    static final Logger LOG = LoggerFactory.getLogger(ReadController.class);
    final JsonFactory factory;
    @Inject
    ObjectMapper jacksonMapper;

    public ReadController(ObjectMapper jacksonMapper) {
        this.jacksonMapper = jacksonMapper;
        factory = new JsonFactory();
        factory.setCodec(jacksonMapper);
    }

    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Get("{path}")
    public Mono<Long> get(final String path) throws IOException {
        File file = new File(path);
        return parseFile(file);
    }

    private Mono<Long> parseFile(File file) throws IOException {
        final FileInputStream inputStream = new FileInputStream(file);
        final JsonParser parser = factory.createParser(inputStream);

        if (parser.nextToken() != JsonToken.START_ARRAY) {
            LOG.error("Expected an array");
            return Mono.just(-1L);
        }

        return Flux.<Long>generate(sink -> {
                    try {
                        var nextToken = parser.nextToken();
                        if (nextToken != JsonToken.START_OBJECT) {
                            sink.complete();
                            return;
                        }

                        var model = parser.readValueAs(MyModel.class);
                        sink.next((long)model.amount);

                    } catch (Exception e) {
                        LOG.error("Error ", e);
                        sink.error(e);
                    }

                })
                .onBackpressureBuffer()
                .reduce(0L, Long::sum);
    }

}
