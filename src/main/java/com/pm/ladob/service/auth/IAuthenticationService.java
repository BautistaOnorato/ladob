package com.pm.ladob.service.auth;

import com.pm.ladob.dto.auth.LoginRequestDto;
import com.pm.ladob.dto.auth.LoginResponseDto;
import com.pm.ladob.dto.auth.RegisterRequestDto;
import com.pm.ladob.dto.auth.RegisterResponseDto;

public interface IAuthenticationService {
    LoginResponseDto login(LoginRequestDto request);
    RegisterResponseDto register(RegisterRequestDto request);
}
