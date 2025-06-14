package com.pm.ladob.auth;

import com.pm.ladob.dto.auth.LoginRequestDto;
import com.pm.ladob.dto.auth.LoginResponseDto;
import com.pm.ladob.dto.auth.RegisterRequestDto;
import com.pm.ladob.dto.auth.RegisterResponseDto;
import com.pm.ladob.dto.user.UserResponseDto;
import com.pm.ladob.exceptions.AlreadyExistsException;
import com.pm.ladob.models.User;
import com.pm.ladob.security.JwtService;
import com.pm.ladob.service.auth.AuthenticationService;
import com.pm.ladob.service.user.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JwtService jwtService;
    @Mock
    private UserService userService;

    @InjectMocks
    private AuthenticationService authService;

    @Test
    void itShouldRegisterUser() {
        RegisterRequestDto request = RegisterRequestDto.builder()
                .email("newuser@test.com")
                .password("password")
                .firstName("User")
                .lastName("Test")
                .build();

        UserResponseDto userResponseDto = UserResponseDto.builder()
                .email("newuser@test.com")
                .build();

        Mockito.when(userService.createUser(Mockito.any())).thenReturn(userResponseDto);

        RegisterResponseDto result = authService.register(request);

        Assertions.assertNotNull(result);
        Assertions.assertEquals("newuser@test.com", result.getEmail());

        Mockito.verify(userService, Mockito.times(1)).createUser(Mockito.any());
    }

    @Test
    void itShouldNotRegisterUsersWithSameEmail() {
        RegisterRequestDto request = RegisterRequestDto.builder()
                .email("newuser@test.com")
                .password("password")
                .lastName("Test")
                .build();

        Mockito.when(userService.createUser(Mockito.any()))
                .thenThrow(new AlreadyExistsException("User already exists with email: " + request.getEmail()));

        Assertions.assertThrows(
                AlreadyExistsException.class,
                () -> authService.register(request)
        );

        Mockito.verify(userService, Mockito.times(1)).createUser(Mockito.any());
    }

    @Test
    void itShouldLoginUser() {
        LoginRequestDto request = LoginRequestDto.builder()
                .email("user@test.com")
                .password("password")
                .build();

        User mockUser = new User();
        mockUser.setEmail("user@test.com");

        Authentication authentication = Mockito.mock(Authentication.class);
        Mockito.when(authentication.getPrincipal()).thenReturn(mockUser);

        Mockito.when(authenticationManager.authenticate(Mockito.any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        Mockito.when(jwtService.generateToken(mockUser)).thenReturn("mocked-jwt-token");

        LoginResponseDto result = authService.login(request);

        Assertions.assertNotNull(result);
        Assertions.assertEquals("mocked-jwt-token", result.getToken());

        Mockito.verify(authenticationManager, Mockito.times(1)).authenticate(Mockito.any(UsernamePasswordAuthenticationToken.class));
        Mockito.verify(jwtService, Mockito.times(1)).generateToken(mockUser);
    }
}
