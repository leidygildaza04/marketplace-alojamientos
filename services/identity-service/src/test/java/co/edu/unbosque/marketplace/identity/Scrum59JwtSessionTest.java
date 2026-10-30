package co.edu.unbosque.marketplace.identity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import co.edu.unbosque.marketplace.identity.domain.Role;
import co.edu.unbosque.marketplace.identity.domain.User;
import co.edu.unbosque.marketplace.identity.repository.UserRepository;
import co.edu.unbosque.marketplace.identity.security.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

/** Pruebas de SCRUM-59: autenticación y generación del token de sesión. */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class Scrum59JwtSessionTest {

    private static final String PASSWORD = "Clave12345!";

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private UserRepository users;
    @Autowired private JwtService jwtService;

    @Test
    void loginGeneratesSignedTokenWithUserClaimsAndExpiration() throws Exception {
        String email = "scrum59-" + UUID.randomUUID() + "@example.com";
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "email", email,
                                "password", PASSWORD,
                                "fullName", "Usuario SCRUM 59",
                                "role", "HOST"))))
                .andExpect(status().isCreated());

        User user = users.findByEmail(email).orElseThrow();
        String response = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "email", email,
                                "password", PASSWORD))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.role").value("HOST"))
                .andExpect(jsonPath("$.expiresInMinutes").value(5))
                .andReturn().getResponse().getContentAsString();

        String token = objectMapper.readTree(response).get("token").asText();
        assertNotNull(token);
        Claims claims = jwtService.parse(token);
        assertEquals(String.valueOf(user.getId()), claims.getSubject());
        assertEquals(email, claims.get("email", String.class));
        assertEquals(Role.HOST.name(), claims.get("role", String.class));
        assertNotNull(claims.getIssuedAt());
        assertNotNull(claims.getExpiration());
        assertEquals(5L, (claims.getExpiration().getTime() - claims.getIssuedAt().getTime()) / 60_000L);
        assertEquals(true, claims.getExpiration().toInstant().isAfter(Instant.now()));
    }
}
