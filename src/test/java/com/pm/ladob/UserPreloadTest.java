package com.pm.ladob;
import com.pm.ladob.config.TestUserDataLoader;
import com.pm.ladob.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@Import(TestUserDataLoader.class)
public class UserPreloadTest {
    @Autowired
    private UserRepository userRepository;

    @Test
    void testUserArePreloaded() {
        Assertions.assertTrue(userRepository.existsByEmail("admin@gmail.com"));
        Assertions.assertTrue(userRepository.existsByEmail("user@gmail.com"));
    }
}
