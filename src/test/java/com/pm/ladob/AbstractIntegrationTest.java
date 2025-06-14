package com.pm.ladob;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pm.ladob.enums.UserRole;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class AbstractIntegrationTest {
    @Autowired
    protected MockMvc mockMvc;

    protected String userToken;
    protected String adminToken;

    String getUserToken() throws Exception {
        String loginPayload = """
                {
                    "email": "user@gmail.com",
                    "password": "password"
                }
        """;

        String response = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginPayload))
                .andReturn()
                .getResponse()
                .getContentAsString();

        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(response);
        return jsonNode.get("token").asText();
    }

    String getAdminToken() throws Exception {
        String loginPayload = """
                {
                    "email": "admin@gmail.com",
                    "password": "password"
                }
        """;

        String response = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginPayload))
                .andReturn()
                .getResponse()
                .getContentAsString();

        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(response);
        return jsonNode.get("token").asText();
    }

    @BeforeAll
    void authenticate() throws Exception {
        userToken = getUserToken();
        adminToken = getAdminToken();
    }

    protected RequestPostProcessor authToken(UserRole role) {
        return request -> {
            request.addHeader("Authorization", "Bearer " + (role == UserRole.ADMIN ? adminToken : userToken));
            return request;
        };
    }
}
