package com.realnest.service;

import com.realnest.dto.AuthDTO;
import com.realnest.dto.LoginDTO;
import com.realnest.dto.RegisterDTO;

public interface AuthService {
    AuthDTO register(RegisterDTO dto);
    AuthDTO login(LoginDTO dto);
}
