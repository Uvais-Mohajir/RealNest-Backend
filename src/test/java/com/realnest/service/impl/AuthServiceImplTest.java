package com.realnest.service.impl;

import com.realnest.dto.AuthDTO;
import com.realnest.dto.LoginDTO;
import com.realnest.dto.RegisterDTO;
import com.realnest.entity.User;
import com.realnest.enums.Role;
import com.realnest.exception.UnauthorizedException;
import com.realnest.mapper.UserMapper;
import com.realnest.repository.UserRepository;
import com.realnest.security.JwtTokenProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JwtTokenProvider jwtTokenProvider;
    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private AuthServiceImpl authService;

    @Test
    void register_shouldThrow_whenEmailAlreadyExists() {
        RegisterDTO dto = new RegisterDTO();
        dto.setEmail("user@realnest.com");
        dto.setRole(Role.CUSTOMER);

        when(userRepository.existsByEmail("user@realnest.com")).thenReturn(true);

        assertThrows(UnauthorizedException.class, () -> authService.register(dto));
    }

    @Test
    void register_shouldThrow_whenRoleIsAdmin() {
        RegisterDTO dto = new RegisterDTO();
        dto.setEmail("admin@realnest.com");
        dto.setRole(Role.ADMIN);

        when(userRepository.existsByEmail("admin@realnest.com")).thenReturn(false);

        assertThrows(UnauthorizedException.class, () -> authService.register(dto));
    }

    @Test
    void register_shouldReturnAuthDto_whenValid() {
        RegisterDTO dto = new RegisterDTO();
        dto.setName("Alice");
        dto.setEmail("alice@realnest.com");
        dto.setPassword("secret123");
        dto.setRole(Role.CUSTOMER);

        User mapped = User.builder().name("Alice").email("alice@realnest.com").password("secret123").role(Role.CUSTOMER).build();
        User saved = User.builder().id(1L).name("Alice").email("alice@realnest.com").password("encoded").role(Role.CUSTOMER).build();
        AuthDTO auth = new AuthDTO("jwt-token", 1L, "Alice", "alice@realnest.com", Role.CUSTOMER);

        when(userRepository.existsByEmail("alice@realnest.com")).thenReturn(false);
        when(userMapper.toEntity(dto)).thenReturn(mapped);
        when(passwordEncoder.encode("secret123")).thenReturn("encoded");
        when(userRepository.save(mapped)).thenReturn(saved);
        when(jwtTokenProvider.generateToken("alice@realnest.com", "CUSTOMER")).thenReturn("jwt-token");
        when(userMapper.toAuthDto(saved, "jwt-token")).thenReturn(auth);

        AuthDTO result = authService.register(dto);

        assertEquals("jwt-token", result.getToken());
        assertEquals(Role.CUSTOMER, result.getRole());
        verify(userRepository).save(mapped);
    }

    @Test
    void login_shouldReturnAuthDto() {
        LoginDTO dto = new LoginDTO();
        dto.setEmail("user@realnest.com");
        dto.setPassword("secret123");
        dto.setRole(Role.CUSTOMER);

        Authentication authentication = new UsernamePasswordAuthenticationToken("user@realnest.com", "secret123");
        User user = User.builder()
                .id(1L)
                .name("Test User")
                .email("user@realnest.com")
                .password("encoded")
                .role(Role.CUSTOMER)
                .build();
        AuthDTO authDTO = new AuthDTO("jwt-token", 1L, "Test User", "user@realnest.com", Role.CUSTOMER);

        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(jwtTokenProvider.generateToken(anyString(), anyString())).thenReturn("jwt-token");
        when(userMapper.toAuthDto(user, "jwt-token")).thenReturn(authDTO);

        AuthDTO result = authService.login(dto);

        assertEquals("jwt-token", result.getToken());
        assertEquals(Role.CUSTOMER, result.getRole());
        verify(userMapper).toAuthDto(user, "jwt-token");
    }

    @Test
    void login_shouldThrow_whenUserNotFoundAfterAuthentication() {
        LoginDTO dto = new LoginDTO();
        dto.setEmail("missing@realnest.com");
        dto.setPassword("secret123");
        dto.setRole(Role.CUSTOMER);

        Authentication authentication = new UsernamePasswordAuthenticationToken("missing@realnest.com", "secret123");
        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(userRepository.findByEmail("missing@realnest.com")).thenReturn(Optional.empty());

        assertThrows(UnauthorizedException.class, () -> authService.login(dto));
    }

    @Test
    void login_shouldThrow_whenRoleDoesNotMatch() {
        LoginDTO dto = new LoginDTO();
        dto.setEmail("user@realnest.com");
        dto.setPassword("secret123");
        dto.setRole(Role.OWNER);

        Authentication authentication = new UsernamePasswordAuthenticationToken("user@realnest.com", "secret123");
        User user = User.builder()
                .id(1L)
                .name("Test User")
                .email("user@realnest.com")
                .password("encoded")
                .role(Role.CUSTOMER)
                .build();

        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));

        assertThrows(UnauthorizedException.class, () -> authService.login(dto));
    }
}
