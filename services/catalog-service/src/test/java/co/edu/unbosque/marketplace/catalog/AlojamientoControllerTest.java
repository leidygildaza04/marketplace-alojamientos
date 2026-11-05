
package co.edu.unbosque.marketplace.catalog;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;

import co.edu.unbosque.marketplace.catalog.domain.Alojamiento;
import co.edu.unbosque.marketplace.catalog.repository.AlojamientoRepository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:catalog_alojamiento_test",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.flyway.enabled=false"
})
@AutoConfigureMockMvc
@Transactional
class AlojamientoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AlojamientoRepository alojamientoRepository;

    // Simula Backoffice para que las pruebas del controlador no dependan de un servicio externo.
    @MockitoBean
    private co.edu.unbosque.marketplace.catalog.client.AuditClient auditClient;

    // PRUEBAS ORIGINALES

    @Test
    @WithMockUser(username = "100", roles = "HOST")
    void permiteRegistrarAlojamientoComoAnfitrion() throws Exception {
        mockMvc.perform(post("/api/alojamientos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "hostId": 200,
                                    "titulo": "Apartamento de prueba",
                                    "descripcion": "Alojamiento para pruebas",
                                    "direccion": "Calle 10",
                                    "ciudad": "Bogota",
                                    "precioNoche": 150000,
                                    "capacidad": 4
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.hostId").value(100))
                .andExpect(jsonPath("$.titulo").value("Apartamento de prueba"))
                .andExpect(jsonPath("$.estado").value("BORRADOR"));
    }

    @Test
    void rechazaRegistroSinAutenticacion() throws Exception {
        mockMvc.perform(post("/api/alojamientos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "titulo": "Apartamento de prueba",
                                    "precioNoche": 150000,
                                    "capacidad": 4
                                }
                                """))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "200", roles = "GUEST")
    void rechazaRegistroCuandoElUsuarioNoEsAnfitrion() throws Exception {
        mockMvc.perform(post("/api/alojamientos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "titulo": "Apartamento de prueba",
                                    "precioNoche": 150000,
                                    "capacidad": 4
                                }
                                """))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "100", roles = "HOST")
    void rechazaDatosObligatoriosInvalidos() throws Exception {
        mockMvc.perform(post("/api/alojamientos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "titulo": "",
                                    "precioNoche": 0,
                                    "capacidad": 0
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    // PRUEBAS DE PRECIO

    @Test
    @WithMockUser(username = "100", roles = "HOST")
    void rechazaPrecioEnCero() throws Exception {
        mockMvc.perform(post("/api/alojamientos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "titulo": "Apartamento de prueba",
                                    "precioNoche": 0,
                                    "capacidad": 4
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "100", roles = "HOST")
    void rechazaPrecioNegativo() throws Exception {
        mockMvc.perform(post("/api/alojamientos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "titulo": "Apartamento de prueba",
                                    "precioNoche": -100,
                                    "capacidad": 4
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "100", roles = "HOST")
    void rechazaPrecioNulo() throws Exception {
        mockMvc.perform(post("/api/alojamientos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "titulo": "Apartamento de prueba",
                                    "precioNoche": null,
                                    "capacidad": 4
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    // PRUEBAS DE CAPACIDAD

    @Test
    @WithMockUser(username = "100", roles = "HOST")
    void rechazaCapacidadEnCero() throws Exception {
        mockMvc.perform(post("/api/alojamientos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "titulo": "Apartamento de prueba",
                                    "precioNoche": 150000,
                                    "capacidad": 0
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "100", roles = "HOST")
    void rechazaCapacidadNegativa() throws Exception {
        mockMvc.perform(post("/api/alojamientos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "titulo": "Apartamento de prueba",
                                    "precioNoche": 150000,
                                    "capacidad": -1
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "100", roles = "HOST")
    void rechazaCapacidadNula() throws Exception {
        mockMvc.perform(post("/api/alojamientos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "titulo": "Apartamento de prueba",
                                    "precioNoche": 150000,
                                    "capacidad": null
                                }
                                """))
                .andExpect(status().isBadRequest());
    }


    @Test
    @WithMockUser(username = "200", roles = "ADMIN")
    void administradorPuedeActivarAlojamientoDeOtroUsuario() throws Exception {

        Alojamiento alojamiento = alojamientoRepository.save(
                new Alojamiento(
                        100L,
                        "Apartamento administrador",
                        "Alojamiento para pruebas",
                        "Calle 10",
                        "Bogota",
                        new BigDecimal("150000"),
                        4
                )
        );

        mockMvc.perform(post("/api/alojamientos/"
                        + alojamiento.getId() + "/activar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("ACTIVA"));
    }

    @Test
    @WithMockUser(username = "200", roles = "HOST")
    void anfitrionNoPuedeActivarAlojamientoDeOtroUsuario() throws Exception {

        Alojamiento alojamiento = alojamientoRepository.save(
                new Alojamiento(
                        100L,
                        "Apartamento de otro usuario",
                        "Alojamiento para pruebas",
                        "Calle 10",
                        "Bogota",
                        new BigDecimal("150000"),
                        4
                )
        );

        mockMvc.perform(post("/api/alojamientos/"
                        + alojamiento.getId() + "/activar"))
                .andExpect(status().isForbidden());
    }


    // PRUEBA DE VALORES MINIMOS VALIDOS

    @Test
    @WithMockUser(username = "100", roles = "HOST")
    void permitePrecioYCapacidadEnValoresMinimos() throws Exception {
        mockMvc.perform(post("/api/alojamientos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "titulo": "Apartamento de prueba",
                                    "precioNoche": 0.01,
                                    "capacidad": 1
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.hostId").value(100));
    }

    // PRUEBAS DE ACTIVACION - SCRUM-70

    @Test
    @WithMockUser(username = "100", roles = "HOST")
    void activaAlojamientoCuandoCumpleTodosLosRequisitos() throws Exception {

        Alojamiento alojamiento = alojamientoRepository.save(
                new Alojamiento(
                        100L,
                        "Apartamento completo",
                        "Alojamiento para pruebas",
                        "Calle 10",
                        "Bogota",
                        new BigDecimal("150000"),
                        4
                )
        );

        mockMvc.perform(post("/api/alojamientos/"
                        + alojamiento.getId() + "/activar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("ACTIVA"));
    }

    @Test
    @WithMockUser(username = "100", roles = "HOST")
    void rechazaActivacionCuandoFaltanDatosYConservaEstado() throws Exception {

        Alojamiento alojamiento = alojamientoRepository.save(
                new Alojamiento(
                        100L,
                        "Apartamento incompleto",
                        null,
                        null,
                        "Bogota",
                        new BigDecimal("150000"),
                        4
                )
        );

        mockMvc.perform(post("/api/alojamientos/"
                        + alojamiento.getId() + "/activar"))
                .andExpect(status().isBadRequest())
                .andExpect(status().reason(
                        containsString("Campos faltantes")
                ));

        Alojamiento alojamientoGuardado = alojamientoRepository
                .findById(alojamiento.getId())
                .orElseThrow();

        assertEquals("BORRADOR", alojamientoGuardado.getEstado());
    }
}
