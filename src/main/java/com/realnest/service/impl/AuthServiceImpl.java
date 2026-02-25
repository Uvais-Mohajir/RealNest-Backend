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
import com.realnest.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserMapper userMapper;

    @Override
    public AuthDTO register(RegisterDTO dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new UnauthorizedException("Email already exists");
        }
        if (dto.getRole() == Role.ADMIN) {
            throw new UnauthorizedException("Admin registration is not allowed");
        }

        User user = userMapper.toEntity(dto);
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        User saved = userRepository.save(user);
        String token = jwtTokenProvider.generateToken(saved.getEmail(), saved.getRole().name());
        return userMapper.toAuthDto(saved, token);
    }

    @Override
    public AuthDTO login(LoginDTO dto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword())
        );
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("User not found"));
        if (user.getRole() != dto.getRole()) {
            throw new UnauthorizedException("Invalid role for this user");
        }
        String token = jwtTokenProvider.generateToken(user.getEmail(), user.getRole().name());
        return userMapper.toAuthDto(user, token);
    }
}
