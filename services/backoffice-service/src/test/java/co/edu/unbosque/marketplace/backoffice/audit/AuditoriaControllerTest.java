package co.edu.unbosque.marketplace.backoffice.audit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import co.edu.unbosque.marketplace.backoffice.audit.repository.EventoAuditoriaRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
class AuditoriaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EventoAuditoriaRepository repository;

    @Test
    void rechazaSolicitudSinSecretoInterno() throws Exception {
        mockMvc.perform(post("/internal/auditoria/eventos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"idUsuario\":7,\"accion\":\"ACTIVAR_ALOJAMIENTO\","
                                + "\"entidad\":\"ALOJAMIENTO\",\"idEntidad\":15,"
                                + "\"detalle\":\"Activación del alojamiento\","
                                + "\"resultado\":\"EXITOSO\"}"))
                .andExpect(status().isUnauthorized());

        assertThat(repository.count()).isZero();
    }

    @Test
    void registraEventoCuandoElSecretoEsValido() throws Exception {
        mockMvc.perform(post("/internal/auditoria/eventos")
                        .header("X-Internal-Secret", "test-secret")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"idUsuario\":7,\"accion\":\"ACTIVAR_ALOJAMIENTO\","
                                + "\"entidad\":\"ALOJAMIENTO\",\"idEntidad\":15,"
                                + "\"detalle\":\"Activación del alojamiento\","
                                + "\"resultado\":\"EXITOSO\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idUsuario").value(7))
                .andExpect(jsonPath("$.accion").value("ACTIVAR_ALOJAMIENTO"))
                .andExpect(jsonPath("$.entidad").value("ALOJAMIENTO"))
                .andExpect(jsonPath("$.idEntidad").value(15))
                .andExpect(jsonPath("$.resultado").value("EXITOSO"))
                .andExpect(jsonPath("$.fechaEvento").exists());

        assertThat(repository.count()).isEqualTo(1);
    }
}
