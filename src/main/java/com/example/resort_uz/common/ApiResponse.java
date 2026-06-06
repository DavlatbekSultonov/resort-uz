package com.example.resort_uz.common;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse {

    private boolean success;
    private String message;
    private Object data;

    public static ApiResponse ok(Object data) {
        return ApiResponse.builder().success(true).message("OK").data(data).build();
    }

    public static ApiResponse ok(String message) {
        return ApiResponse.builder().success(true).message(message).build();
    }

    public static ApiResponse ok(String message, Object data) {
        return ApiResponse.builder().success(true).message(message).data(data).build();
    }

    public static ApiResponse error(String message) {
        return ApiResponse.builder().success(false).message(message).build();
    }
}
