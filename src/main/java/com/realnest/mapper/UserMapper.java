package com.realnest.mapper;

import com.realnest.dto.AuthDTO;
import com.realnest.dto.RegisterDTO;
import com.realnest.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public User toEntity(RegisterDTO dto) {
        return User.builder()
                .name(dto.getName())
                .email(dto.getEmail())
                .password(dto.getPassword())
                .role(dto.getRole())
                .build();
    }

    public AuthDTO toAuthDto(User user, String token) {
        return new AuthDTO(token, user.getId(), user.getName(), user.getEmail(), user.getRole());
    }
}
