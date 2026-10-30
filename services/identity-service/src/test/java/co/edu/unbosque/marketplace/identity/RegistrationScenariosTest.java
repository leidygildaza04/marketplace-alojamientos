
package co.edu.unbosque.marketplace.identity;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import co.edu.unbosque.marketplace.identity.domain.User;
import co.edu.unbosque.marketplace.identity.repository.UserRepository;
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

/** Pruebas de aceptación para los escenarios principales de SCRUM-58. */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class RegistrationScenariosTest {

    private static final String PASSWORD = "Clave12345!";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository users;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private static String uniqueEmail() {
        return "scrum58-" + UUID.randomUUID() + "@example.com";
    }

    private org.springframework.test.web.servlet.ResultActions register(
            String email, String password, String role) throws Exception {
        return mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of(
                        "email", email,
                        "password", password,
                        "fullName", "Usuario SCRUM 58",
                        "role", role))));
    }

    @Test
    void validRegistrationPersistsUserWithEncodedPasswordAndSafeResponse() throws Exception {
        String email = uniqueEmail();

        register(email, PASSWORD, "GUEST")
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value(email))
                .andExpect(jsonPath("$.role").value("GUEST"))
                .andExpect(content().string(not(containsString(PASSWORD))))
                .andExpect(content().string(not(containsString("passwordHash"))));

        User saved = users.findByEmail(email).orElseThrow();
        org.junit.jupiter.api.Assertions.assertNotNull(saved.getId());
        org.junit.jupiter.api.Assertions.assertTrue(passwordEncoder.matches(PASSWORD, saved.getPasswordHash()));
        org.junit.jupiter.api.Assertions.assertNotEquals(PASSWORD, saved.getPasswordHash());
    }

    @Test
    void duplicateEmailIgnoringLetterCaseReturnsConflictAndKeepsOneUser() throws Exception {
        String email = uniqueEmail();

        register(email, PASSWORD, "GUEST").andExpect(status().isCreated());
        register(email.toUpperCase(java.util.Locale.ROOT), PASSWORD, "GUEST")
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("EMAIL_ALREADY_USED"));

        org.junit.jupiter.api.Assertions.assertTrue(users.findByEmail(email).isPresent());
        org.junit.jupiter.api.Assertions.assertTrue(users.findByEmail(email.toUpperCase(java.util.Locale.ROOT)).isEmpty());
    }

    @Test
    void adminRoleIsRejectedAndUserIsNotPersisted() throws Exception {
        String email = uniqueEmail();

        register(email, PASSWORD, "ADMIN")
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("ROLE_NOT_ALLOWED"));

        org.junit.jupiter.api.Assertions.assertTrue(users.findByEmail(email).isEmpty());
    }
}
