package com.realnest.dto;

import com.realnest.enums.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Schema(description = "Authentication response payload")
public class AuthDTO {
    @Schema(description = "JWT access token")
    private String token;
    @Schema(description = "Authenticated user id", example = "1")
    private Long userId;
    @Schema(description = "Authenticated user name", example = "John Doe")
    private String name;
    @Schema(description = "Authenticated user email", example = "john@example.com")
    private String email;
    @Schema(description = "Authenticated user role", example = "CUSTOMER")
    private Role role;
}
