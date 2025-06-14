package com.pm.ladob.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pm.ladob.config.TestUserDataLoader;
import com.pm.ladob.dto.auth.LoginRequestDto;
import com.pm.ladob.dto.auth.RegisterRequestDto;
import com.pm.ladob.enums.UserRole;
import com.pm.ladob.models.User;
import com.pm.ladob.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class AuthControllerMVCTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void itShouldRegisterUser() throws Exception {
        RegisterRequestDto request = RegisterRequestDto.builder()
                .email("newuser@test.com")
                .password("password")
                .firstName("user")
                .lastName("test")
                .build();

        mockMvc.perform(post("/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("newuser@test.com"))
                .andExpect(jsonPath("$.active").value(false));

        Assertions.assertEquals(1, userRepository.count());
    }

    @Test
    void itShouldNotRegisterUserWithMissingFields() throws Exception {
        RegisterRequestDto request = RegisterRequestDto.builder().build();

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode").value(400))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.errors.length()").value(4));

        Assertions.assertEquals(0, userRepository.count());
    }

    @Test
    void itShouldNotRegisterAlreadyExistingUser() throws Exception {
        User user = User.builder()
                .email("newuser@test.com")
                .password("password")
                .firstName("user")
                .lastName("test")
                .active(false)
                .role(UserRole.USER)
                .build();

        userRepository.save(user);

        RegisterRequestDto request = RegisterRequestDto.builder()
                .email("newuser@test.com")
                .password("passowrd")
                .firstName("user")
                .lastName("test")
                .build();

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode").value(400))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.errors.message").value("User already exists with email: " + request.getEmail()));

        Assertions.assertEquals(1, userRepository.count());
    }

    @Test
    void itShouldNotRegisterUserWithPasswordLengthUnder8() throws Exception {
        RegisterRequestDto request = RegisterRequestDto.builder()
                .email("newuser@test.com")
                .password("pass")
                .firstName("user")
                .lastName("test")
                .build();

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode").value(400))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.errors.password").value("Password must be at least 8 characters long"));

        Assertions.assertEquals(0, userRepository.count());
    }

    @Test
    void itShouldNotRegisterUserWithInvalidEmail() throws Exception {
        RegisterRequestDto request = RegisterRequestDto.builder()
                .email("newusertest.com")
                .password("password")
                .firstName("user")
                .lastName("test")
                .build();

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode").value(400))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.errors.email").value("Email should be a valid email address"));

        Assertions.assertEquals(0, userRepository.count());
    }

    @Test
    void itShouldLoginUser() throws Exception {
        User user = User.builder()
                .email("newuser@test.com")
                .password(passwordEncoder.encode("password"))
                .firstName("user")
                .lastName("test")
                .active(false)
                .role(UserRole.USER)
                .build();

        userRepository.save(user);

        LoginRequestDto request = LoginRequestDto.builder()
                .email("newuser@test.com")
                .password("password")
                .build();

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());
    }

    @Test
    void itShouldNotLoginUserWithMissingFields() throws Exception {
        User user = User.builder()
                .email("newuser@test.com")
                .password(passwordEncoder.encode("password"))
                .firstName("user")
                .lastName("test")
                .active(false)
                .role(UserRole.USER)
                .build();

        userRepository.save(user);

        LoginRequestDto request = LoginRequestDto.builder().build();

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode").value(400))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.errors.length()").value(2));
    }

    @Test
    void itShouldNotLoginUserWithInvalidEmail() throws Exception {
        User user = User.builder()
                .email("newuser@test.com")
                .password(passwordEncoder.encode("password"))
                .firstName("user")
                .lastName("test")
                .active(false)
                .role(UserRole.USER)
                .build();

        userRepository.save(user);

        LoginRequestDto request = LoginRequestDto.builder()
                .email("newusertest.com")
                .password("password")
                .build();

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode").value(400))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.errors.email").value("Email should be a valid email address"));
    }

    @Test
    void itShouldNotLoginUserWithPasswordLengthuUnder8() throws Exception {
        User user = User.builder()
                .email("newuser@test.com")
                .password(passwordEncoder.encode("password"))
                .firstName("user")
                .lastName("test")
                .active(false)
                .role(UserRole.USER)
                .build();

        userRepository.save(user);

        LoginRequestDto request = LoginRequestDto.builder()
                .email("newuser@test.com")
                .password("pass")
                .build();

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode").value(400))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.errors.password").value("Password must be at least 8 characters long"));
    }

    @Test
    void itShouldNotLoginUserWithBadCredentials() throws Exception {
        User user = User.builder()
                .email("newuser@test.com")
                .password(passwordEncoder.encode("password"))
                .firstName("user")
                .lastName("test")
                .active(false)
                .role(UserRole.USER)
                .build();

        userRepository.save(user);

        LoginRequestDto request = LoginRequestDto.builder()
                .email("wronguser@test.com")
                .password("password")
                .build();

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.statusCode").value(401))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.errors.message").value("Bad credentials"));
    }
}
