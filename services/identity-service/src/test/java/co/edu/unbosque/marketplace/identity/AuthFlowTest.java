
package co.edu.unbosque.marketplace.identity;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

/** Pruebas de HU-01 (registro con rol), HU-02 (inicio de sesión) y ruta protegida. */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthFlowTest {

    private static final String PASSWORD = "Clave12345!";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static String uniqueEmail() {
        return "user-" + UUID.randomUUID() + "@example.com";
    }

    private ResultActions register(String email, String password, String role) throws Exception {
        return mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of(
                        "email", email,
                        "password", password,
                        "fullName", "Persona de Prueba",
                        "role", role))));
    }

    private ResultActions login(String email, String password) throws Exception {
        return mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(
                        Map.of("email", email, "password", password))));
    }

    @Test
    void registerGuestReturnsCreatedWithoutPassword() throws Exception {
        register(uniqueEmail(), PASSWORD, "GUEST")
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.role").value("GUEST"))
                .andExpect(jsonPath("$.active").value(true))
                .andExpect(content().string(not(containsString("password"))));
    }

    @Test
    void registerHostIsAllowed() throws Exception {
        register(uniqueEmail(), PASSWORD, "HOST")
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.role").value("HOST"));
    }

    @Test
    void registerWithDuplicatedEmailReturnsConflict() throws Exception {
        String email = uniqueEmail();

        register(email, PASSWORD, "GUEST")
                .andExpect(status().isCreated());

        register(email.toUpperCase(), PASSWORD, "GUEST")
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("EMAIL_ALREADY_USED"));
    }

    @Test
    void registerAsAdminIsForbidden() throws Exception {
        register(uniqueEmail(), PASSWORD, "ADMIN")
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("ROLE_NOT_ALLOWED"));
    }

    @Test
    void registerWithShortPasswordReturnsBadRequest() throws Exception {
        register(uniqueEmail(), "corta", "GUEST")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }

    @Test
    void registerWithUnknownRoleReturnsBadRequest() throws Exception {
        register(uniqueEmail(), PASSWORD, "SUPERUSER")
                .andExpect(status().isBadRequest());
    }

    @Test
    void loginWithValidCredentialsReturnsToken() throws Exception {
        String email = uniqueEmail();

        register(email, PASSWORD, "HOST")
                .andExpect(status().isCreated());

        login(email, PASSWORD)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.role").value("HOST"));
    }

    @Test
    void loginWithWrongPasswordReturnsUnauthorized() throws Exception {
        String email = uniqueEmail();

        register(email, PASSWORD, "GUEST")
                .andExpect(status().isCreated());

        login(email, "otraClave999")
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("INVALID_CREDENTIALS"));
    }

    @Test
    void protectedRouteWithoutTokenReturnsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/users/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void protectedRouteWithTokenReturnsCurrentUser() throws Exception {
        String email = uniqueEmail();

        register(email, PASSWORD, "GUEST")
                .andExpect(status().isCreated());

        String body = login(email, PASSWORD)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String token = objectMapper.readTree(body)
                .get("token")
                .asText();

        mockMvc.perform(get("/api/users/me")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(email));
    }

    @Test
    void protectedRouteWithInvalidTokenReturnsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/users/me")
                        .header("Authorization", "Bearer token-falso"))
                .andExpect(status().isUnauthorized());
    }
}
