package com.realnest.controller;

import com.realnest.dto.AuthDTO;
import com.realnest.dto.LoginDTO;
import com.realnest.dto.RegisterDTO;
import com.realnest.enums.Role;
import com.realnest.security.CustomUserDetailsService;
import com.realnest.security.JwtAuthenticationFilter;
import com.realnest.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    void register_shouldReturnOkResponse() throws Exception {
        RegisterDTO dto = new RegisterDTO();
        dto.setName("Alice");
        dto.setEmail("alice@realnest.com");
        dto.setPassword("secret123");
        dto.setRole(Role.CUSTOMER);

        when(authService.register(any(RegisterDTO.class)))
                .thenReturn(new AuthDTO("token", 1L, "Alice", "alice@realnest.com", Role.CUSTOMER));

        String json = """
                {"name":"Alice","email":"alice@realnest.com","password":"secret123","role":"CUSTOMER"}
                """;

        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("User registered successfully"))
                .andExpect(jsonPath("$.data.role").value("CUSTOMER"));
    }

    @Test
    void login_shouldReturnOkResponse() throws Exception {
        LoginDTO dto = new LoginDTO();
        dto.setEmail("alice@realnest.com");
        dto.setPassword("secret123");
        dto.setRole(Role.CUSTOMER);

        when(authService.login(any(LoginDTO.class)))
                .thenReturn(new AuthDTO("token", 1L, "Alice", "alice@realnest.com", Role.CUSTOMER));

        String json = """
                {"email":"alice@realnest.com","password":"secret123","role":"CUSTOMER"}
                """;

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Login successful"))
                .andExpect(jsonPath("$.data.email").value("alice@realnest.com"));
    }
}
