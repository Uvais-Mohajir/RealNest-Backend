package com.realnest.dto;

import com.realnest.enums.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Registration request payload")
public class RegisterDTO {
    @NotBlank
    @Schema(description = "Full name", example = "Alice Smith")
    private String name;

    @Email
    @NotBlank
    @Schema(description = "User email address", example = "alice@realnest.com")
    private String email;

    @NotBlank
    @Size(min = 6, max = 100)
    @Schema(description = "Account password", example = "secret123")
    private String password;

    @NotNull
    @Schema(description = "Role for registration", example = "CUSTOMER")
    private Role role;
}
