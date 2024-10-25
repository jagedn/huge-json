package com.example.huge;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.micronaut.serde.annotation.Serdeable;

import java.util.List;

@Serdeable
public class MyModel {
    @JsonProperty
    String name;

    @JsonProperty
    int amount;

    @JsonProperty
    String now;

    @JsonProperty
    List<SubModel> categories;
}
