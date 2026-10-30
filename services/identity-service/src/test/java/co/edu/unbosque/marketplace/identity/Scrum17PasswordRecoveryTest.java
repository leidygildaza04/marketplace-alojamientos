package co.edu.unbosque.marketplace.identity;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import co.edu.unbosque.marketplace.identity.domain.User;
import co.edu.unbosque.marketplace.identity.repository.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class Scrum17PasswordRecoveryTest {
    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper mapper;
    @Autowired UserRepository users;
    @Autowired PasswordEncoder encoder;
    private static final String OLD_PASSWORD = "ClaveAnterior123!";
    private static final String NEW_PASSWORD = "ClaveNueva456!";

    private String createUser() throws Exception {
        String email = "scrum17-" + UUID.randomUUID() + "@example.com";
        mockMvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(Map.of("email", email, "password", OLD_PASSWORD,
                        "fullName", "Usuario SCRUM 17", "role", "GUEST"))))
                .andExpect(status().isCreated());
        return email;
    }

    private String requestToken(String email) throws Exception {
        String body = mockMvc.perform(post("/api/auth/password/recovery")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(Map.of("email", email))))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        return mapper.readTree(body).get("token").asText();
    }

    @Test void registeredEmailReceivesTemporaryToken() throws Exception {
        String email = createUser();
        String token = requestToken(email);
        assertNotNull(token);
        assertEquals(64, token.length());
    }

    @Test void validTokenChangesPasswordAndCanOnlyBeUsedOnce() throws Exception {
        String email = createUser();
        String token = requestToken(email);
        mockMvc.perform(post("/api/auth/password/reset").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(Map.of("token", token, "newPassword", NEW_PASSWORD))))
                .andExpect(status().isNoContent());
        User user = users.findByEmail(email).orElseThrow();
        assertTrue(encoder.matches(NEW_PASSWORD, user.getPasswordHash()));
        assertFalse(encoder.matches(OLD_PASSWORD, user.getPasswordHash()));
        mockMvc.perform(post("/api/auth/password/reset").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(Map.of("token", token, "newPassword", OLD_PASSWORD))))
                .andExpect(status().isBadRequest());
    }

    @Test void invalidTokenIsRejected() throws Exception {
        mockMvc.perform(post("/api/auth/password/reset").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(Map.of("token", "no-es-valido", "newPassword", NEW_PASSWORD))))
                .andExpect(status().isBadRequest());
    }

    @Test void unknownEmailIsRejected() throws Exception {
        mockMvc.perform(post("/api/auth/password/recovery").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(Map.of("email", "no-existe@example.com"))))
                .andExpect(status().isNotFound());
    }
}
