package com.pm.ladob.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
@Data
public class ApiErrorDto {
    private Integer statusCode;
    private String message;
    private Map<String, String> errors;

    public ApiErrorDto (HttpStatus code, Exception ex) {
        this.errors = new HashMap<>();
        this.errors.put("message", ex.getMessage());
        this.statusCode = code.value();
        this.message = code.name();
    }
}
