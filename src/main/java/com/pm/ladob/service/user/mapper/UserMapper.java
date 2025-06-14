package com.pm.ladob.service.user.mapper;

import com.pm.ladob.dto.address.AddressRequestDto;
import com.pm.ladob.dto.user.UserRequestDto;
import com.pm.ladob.dto.user.UserResponseDto;
import com.pm.ladob.models.User;

import java.util.List;

public class UserMapper {
    public static UserResponseDto toDto(User user) {
        return UserResponseDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .active(user.isActive())
                .build();
    }

    public static User toModel(UserRequestDto request) {
        return User.builder()
                .email(request.getEmail())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .password(request.getPassword()).build();
    }
}
