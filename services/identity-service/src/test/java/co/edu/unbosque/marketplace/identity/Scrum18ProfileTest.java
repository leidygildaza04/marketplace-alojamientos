package co.edu.unbosque.marketplace.identity;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
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

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class Scrum18ProfileTest {

    private static final String PASSWORD = "Clave12345!";

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    private String uniqueEmail() { return "profile-" + UUID.randomUUID() + "@example.com"; }

    private String registerAndGetToken(String email, String name) throws Exception {
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of("email", email, "password", PASSWORD,
                        "fullName", name, "role", "GUEST"))))
                .andExpect(status().isCreated());
        String login = mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of("email", email, "password", PASSWORD))))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        JsonNode node = objectMapper.readTree(login);
        return node.get("token").asText();
    }

    @Test
    void authenticatedUserCanConsultBasicProfile() throws Exception {
        String email = uniqueEmail();
        String token = registerAndGetToken(email, "Nombre Inicial");
        mockMvc.perform(get("/api/users/me").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("Nombre Inicial"))
                .andExpect(jsonPath("$.email").value(email))
                .andExpect(jsonPath("$.phone").doesNotExist())
                .andExpect(jsonPath("$.role").value("GUEST"));
    }

    @Test
    void authenticatedUserCanUpdateNameAndPhone() throws Exception {
        String token = registerAndGetToken(uniqueEmail(), "Nombre Inicial");
        mockMvc.perform(put("/api/users/me").header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("fullName", "Nombre Actualizado", "phone", "+573001112233"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("Nombre Actualizado"))
                .andExpect(jsonPath("$.phone").value("+573001112233"));
        mockMvc.perform(get("/api/users/me").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("Nombre Actualizado"))
                .andExpect(jsonPath("$.phone").value("+573001112233"));
    }

    @Test
    void updateWithAlreadyUsedEmailIsRejected() throws Exception {
        String firstEmail = uniqueEmail();
        String secondEmail = uniqueEmail();
        String token = registerAndGetToken(firstEmail, "Usuario Uno");
        registerAndGetToken(secondEmail, "Usuario Dos");
        mockMvc.perform(put("/api/users/me").header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("fullName", "Usuario Uno", "email", secondEmail))))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("EMAIL_ALREADY_USED"));
    }

    @Test
    void updateCannotChangeRole() throws Exception {
        String token = registerAndGetToken(uniqueEmail(), "Usuario");
        mockMvc.perform(put("/api/users/me").header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("fullName", "Usuario", "role", "HOST"))))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("ROLE_NOT_EDITABLE"));
    }

    @Test
    void updateRejectsInvalidPhone() throws Exception {
        String token = registerAndGetToken(uniqueEmail(), "Usuario");
        mockMvc.perform(put("/api/users/me").header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("fullName", "Usuario", "phone", "telefono!"))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }

    @Test
    void profileCannotBeConsultedWithoutAuthentication() throws Exception {
        mockMvc.perform(get("/api/users/me")).andExpect(status().isUnauthorized());
    }
}
