package com.egouletlang.logmein.model.score;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Remaining {

    private Integer raw;

    private String name;

    private Integer count;

    public Remaining(Integer raw, String name, Integer count) {
        this.raw = raw;
        this.name = name;
        this.count = count;
    }

    public String getName() {
        return name;
    }

    public Integer getCount() {
        return count;
    }

    @JsonIgnore
    public Integer getRaw() {
        return raw;
    }
}
