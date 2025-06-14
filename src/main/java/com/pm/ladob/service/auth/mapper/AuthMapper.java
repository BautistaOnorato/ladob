package com.pm.ladob.service.auth.mapper;

import com.pm.ladob.dto.auth.RegisterRequestDto;
import com.pm.ladob.dto.auth.RegisterResponseDto;
import com.pm.ladob.dto.user.UserRequestDto;
import com.pm.ladob.dto.user.UserResponseDto;

public class AuthMapper {
    public static UserRequestDto toUserRequestDto(RegisterRequestDto request) {
        return UserRequestDto.builder()
                .email(request.getEmail())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .password(request.getPassword())
                .build();
    }

    public static RegisterResponseDto userToRegisterResponseDto(UserResponseDto user) {
        return RegisterResponseDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .build();
    }
}
