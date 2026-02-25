package com.realnest.controller;

import com.realnest.dto.ApiDTO;
import com.realnest.dto.AuthDTO;
import com.realnest.dto.LoginDTO;
import com.realnest.dto.RegisterDTO;
import com.realnest.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "User registration and login APIs")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "Register user", description = "Registers a new OWNER or CUSTOMER user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User registered successfully"),
            @ApiResponse(responseCode = "400", description = "Validation failed", content = @Content(schema = @Schema(implementation = ApiDTO.class))),
            @ApiResponse(responseCode = "403", description = "Registration not allowed")
    })
    public ResponseEntity<ApiDTO<AuthDTO>> register(@Valid @RequestBody RegisterDTO dto) {
        return ResponseEntity.ok(ApiDTO.ok("User registered successfully", authService.register(dto)));
    }

    @PostMapping("/login")
    @Operation(summary = "Login user", description = "Authenticates user and returns JWT token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login successful"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials"),
            @ApiResponse(responseCode = "403", description = "Invalid role for user")
    })
    public ResponseEntity<ApiDTO<AuthDTO>> login(@Valid @RequestBody LoginDTO dto) {
        return ResponseEntity.ok(ApiDTO.ok("Login successful", authService.login(dto)));
    }
}
