package com.pm.ladob.config;

import com.pm.ladob.enums.UserRole;
import com.pm.ladob.models.User;
import com.pm.ladob.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

@TestConfiguration
@RequiredArgsConstructor
public class TestUserDataLoader {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @PostConstruct
    public void insertTestUsers() {
        if (userRepository.findByEmail("admin@gmail.com").isEmpty()) {
            userRepository.save(User.builder()
                            .email("admin@gmail.com")
                            .password(passwordEncoder.encode("password"))
                            .firstName("admin")
                            .lastName("admin")
                            .role(UserRole.ADMIN)
                            .active(true)
                            .build());
        }

        if (userRepository.findByEmail("user@gmail.com").isEmpty()) {
            userRepository.save(User.builder()
                    .email("user@gmail.com")
                    .password(passwordEncoder.encode("password"))
                    .firstName("user")
                    .lastName("user")
                    .role(UserRole.USER)
                    .active(true)
                    .build());
        }
    }
}
