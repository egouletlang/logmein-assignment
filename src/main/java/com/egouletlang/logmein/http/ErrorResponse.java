package com.egouletlang.logmein.http;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import springfox.documentation.schema.ModelReference;


@JsonSerialize
public class ErrorResponse{

    @JsonProperty
    private String status;

    @JsonProperty
    private String msg;

    public ErrorResponse(String status, String msg) {
        this.status = status;
        this.msg = msg;
    }

    public static ResponseEntity<?> badRequest(String msg) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ErrorResponse err = new ErrorResponse(status.toString(), msg);
        return new ResponseEntity(err, status);
    }

    public static ResponseEntity<?> conflict(String msg) {
        HttpStatus status = HttpStatus.CONFLICT;
        ErrorResponse err = new ErrorResponse(status.toString(), msg);
        return new ResponseEntity(err, status);
    }

    public static ResponseEntity<?> internalServerError(String msg) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        ErrorResponse err = new ErrorResponse(status.toString(), msg);
        return new ResponseEntity(err, status);
    }
}
