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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getNow() {
        return now;
    }

    public void setNow(String now) {
        this.now = now;
    }

    public List<SubModel> getCategories() {
        return categories;
    }

    public void setCategories(List<SubModel> categories) {
        this.categories = categories;
    }
}
