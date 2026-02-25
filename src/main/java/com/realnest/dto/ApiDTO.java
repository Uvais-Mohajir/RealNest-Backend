package com.realnest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Schema(description = "Standard API response wrapper")
public class ApiDTO<T> {
    @Schema(description = "Indicates whether request succeeded", example = "true")
    private boolean success;
    @Schema(description = "Response message", example = "Operation completed successfully")
    private String message;
    @Schema(description = "Response payload")
    private T data;

    public static <T> ApiDTO<T> ok(String message, T data) {
        return new ApiDTO<>(true, message, data);
    }

    public static <T> ApiDTO<T> fail(String message, T data) {
        return new ApiDTO<>(false, message, data);
    }
}
