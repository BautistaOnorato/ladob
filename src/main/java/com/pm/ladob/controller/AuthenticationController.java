package com.pm.ladob.controller;

import com.pm.ladob.dto.ApiErrorDto;
import com.pm.ladob.dto.auth.LoginRequestDto;
import com.pm.ladob.dto.auth.LoginResponseDto;
import com.pm.ladob.dto.auth.RegisterRequestDto;
import com.pm.ladob.dto.auth.RegisterResponseDto;
import com.pm.ladob.service.auth.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @Operation(summary = "Login user", description = "Login existing user and return token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User logged in successfully",
                    content = @Content(schema = @Schema(implementation = LoginResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Body did not pass validation filters",
                    content = @Content(schema = @Schema(implementation = ApiErrorDto.class)))
    })
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(
            @Valid @RequestBody LoginRequestDto request
    ) {
        return ResponseEntity.ok(authenticationService.login(request));
    }

    @Operation(summary = "Register user", description = "Register new user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User registered successfully",
                    content = @Content(schema = @Schema(implementation = LoginResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Body did not pass validation filters",
                    content = @Content(schema = @Schema(implementation = ApiErrorDto.class)))
    })
    @PostMapping("/register")
    public ResponseEntity<RegisterResponseDto> register(
            @Valid @RequestBody RegisterRequestDto request
    ) {
        return ResponseEntity.ok(authenticationService.register(request));
    }
}
