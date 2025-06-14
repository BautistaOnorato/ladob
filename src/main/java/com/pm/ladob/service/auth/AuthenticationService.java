package com.pm.ladob.service.auth;

import com.pm.ladob.dto.auth.LoginRequestDto;
import com.pm.ladob.dto.auth.LoginResponseDto;
import com.pm.ladob.dto.auth.RegisterRequestDto;
import com.pm.ladob.dto.auth.RegisterResponseDto;
import com.pm.ladob.dto.user.UserResponseDto;
import com.pm.ladob.models.User;
import com.pm.ladob.security.JwtService;
import com.pm.ladob.service.auth.mapper.AuthMapper;
import com.pm.ladob.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService implements IAuthenticationService {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserService userService;

    @Override
    public LoginResponseDto login(LoginRequestDto request) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        final User user = (User) auth.getPrincipal();
        final String token = jwtService.generateToken(user);
        return LoginResponseDto.builder()
                .token(token)
                .build();
    }

    @Override
    public RegisterResponseDto register(RegisterRequestDto request) {
        UserResponseDto response = userService.createUser(AuthMapper.toUserRequestDto(request));

        return AuthMapper.userToRegisterResponseDto(response);
    }
}
