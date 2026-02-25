package com.realnest.dto;

import com.realnest.enums.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Login request payload")
public class LoginDTO {
    @Email
    @NotBlank
    @Schema(description = "User email address", example = "user@realnest.com")
    private String email;

    @NotBlank
    @Schema(description = "Account password", example = "secret123")
    private String password;

    @NotNull
    @Schema(description = "Role to login as", example = "CUSTOMER")
    private Role role;
}
