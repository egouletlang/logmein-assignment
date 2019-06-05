package com.egouletlang.logmein.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.data.annotation.Id;

import java.util.UUID;

public class DbModel {

    @Id
    @ApiModelProperty(notes = "The database generated product ID")
    private String id;

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public void generateId() {
        this.id = UUID.randomUUID().toString();
    }
}