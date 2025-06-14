package com.pm.ladob.service.user;

import com.pm.ladob.dto.user.UserRequestDto;
import com.pm.ladob.dto.user.UserResponseDto;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface IUserService extends UserDetailsService {
    UserResponseDto createUser(UserRequestDto request);
}
