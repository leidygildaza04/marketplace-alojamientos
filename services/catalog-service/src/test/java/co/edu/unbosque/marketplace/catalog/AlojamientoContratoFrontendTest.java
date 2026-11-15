package co.edu.unbosque.marketplace.catalog;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import co.edu.unbosque.marketplace.catalog.client.AuditClient;
import co.edu.unbosque.marketplace.catalog.repository.AlojamientoRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Verifica el contrato HTTP que consume el frontend para crear, editar y listar
 * alojamientos. No es transaccional a propósito: así las respuestas se serializan
 * fuera de la sesión de JPA, igual que en ejecución real.
 */
@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:catalog_contrato_frontend_test",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.flyway.enabled=false",
        "app.jwt.secret=test-jwt-secret-test-jwt-secret-1234567890",
        "app.internal.secret=secret-test-catalog"
})
@AutoConfigureMockMvc
class AlojamientoContratoFrontendTest {

    private static final String CUERPO_COMPLETO = """
            {
                "titulo": "Casa con patio",
                "descripcion": "Casa amplia",
                "direccion": "Calle 10",
                "ciudad": "Cartagena",
                "precioNoche": 320000,
                "capacidad": 8,
                "tipoAlojamiento": "Casa",
                "imagenes": ["https://ejemplo.com/casa.jpg"],
                "servicios": ["Wi-Fi", "Piscina"],
                "reglas": "No fumar",
                "tarifaLimpieza": 55000,
                "politicaCancelacion": "Moderada",
                "serviciosAdicionales": [{"nombre": "Desayuno", "precio": 20000}]
            }
            """;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AlojamientoRepository alojamientos;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuditClient auditClient;

    @AfterEach
    void limpiar() {
        alojamientos.deleteAll();
    }

    private long crearAlojamientoCompleto() throws Exception {
        String respuesta = mockMvc.perform(post("/api/alojamientos")
                        .with(user("100").roles("HOST"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(CUERPO_COMPLETO))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.hostId").value(100))
                .andExpect(jsonPath("$.estado").value("BORRADOR"))
                .andExpect(jsonPath("$.tipoAlojamiento").value("Casa"))
                .andExpect(jsonPath("$.servicios[1]").value("Piscina"))
                .andExpect(jsonPath("$.imagenes[0]").value("https://ejemplo.com/casa.jpg"))
                .andExpect(jsonPath("$.tarifaLimpieza").value(55000))
                .andExpect(jsonPath("$.serviciosAdicionales[0].nombre").value("Desayuno"))
                .andReturn().getResponse().getContentAsString();

        return objectMapper.readTree(respuesta).get("id").asLong();
    }

    @Test
    void anfitrionListaSusAlojamientosConDatosComplementarios() throws Exception {
        long id = crearAlojamientoCompleto();

        mockMvc.perform(get("/api/alojamientos/mios").with(user("100").roles("HOST")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(id))
                .andExpect(jsonPath("$[0].servicios[0]").value("Wi-Fi"))
                .andExpect(jsonPath("$[0].imagenes[0]").value("https://ejemplo.com/casa.jpg"))
                .andExpect(jsonPath("$[0].serviciosAdicionales[0].precio").value(20000));
    }

    @Test
    void listaPublicaYAdministracionIncluyenDatosComplementarios() throws Exception {
        long id = crearAlojamientoCompleto();

        mockMvc.perform(post("/api/alojamientos/" + id + "/activar")
                        .with(user("100").roles("HOST")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("ACTIVA"));

        mockMvc.perform(get("/api/alojamientos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].titulo").value("Casa con patio"))
                .andExpect(jsonPath("$[0].tipoAlojamiento").value("Casa"))
                .andExpect(jsonPath("$[0].servicios.length()").value(2))
                .andExpect(jsonPath("$[0].tarifaLimpieza").value(55000));

        mockMvc.perform(get("/api/alojamientos/administracion")
                        .with(user("1").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].hostId").value(100))
                .andExpect(jsonPath("$[0].imagenes.length()").value(1));
    }

    @Test
    void editarActualizaLosDatosComplementarios() throws Exception {
        long id = crearAlojamientoCompleto();

        mockMvc.perform(put("/api/alojamientos/" + id)
                        .with(user("100").roles("HOST"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "titulo": "Casa renovada",
                                    "descripcion": "Casa amplia",
                                    "direccion": "Calle 10",
                                    "ciudad": "Cartagena",
                                    "precioNoche": 350000,
                                    "capacidad": 6,
                                    "tipoAlojamiento": "Cabaña",
                                    "imagenes": ["https://ejemplo.com/nueva.jpg"],
                                    "servicios": ["Parqueadero"],
                                    "reglas": "No se permiten fiestas",
                                    "tarifaLimpieza": 60000,
                                    "politicaCancelacion": "Flexible",
                                    "serviciosAdicionales": []
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titulo").value("Casa renovada"))
                .andExpect(jsonPath("$.tipoAlojamiento").value("Cabaña"))
                .andExpect(jsonPath("$.servicios.length()").value(1))
                .andExpect(jsonPath("$.servicios[0]").value("Parqueadero"))
                .andExpect(jsonPath("$.imagenes[0]").value("https://ejemplo.com/nueva.jpg"))
                .andExpect(jsonPath("$.reglas").value("No se permiten fiestas"))
                .andExpect(jsonPath("$.politicaCancelacion").value("Flexible"))
                .andExpect(jsonPath("$.tarifaLimpieza").value(60000))
                .andExpect(jsonPath("$.serviciosAdicionales.length()").value(0));
    }

    @Test
    void editarSinDatosComplementariosConservaLosExistentes() throws Exception {
        long id = crearAlojamientoCompleto();

        mockMvc.perform(put("/api/alojamientos/" + id)
                        .with(user("100").roles("HOST"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "titulo": "Casa con patio",
                                    "descripcion": "Casa amplia",
                                    "direccion": "Calle 10",
                                    "ciudad": "Cartagena",
                                    "precioNoche": 330000,
                                    "capacidad": 8
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.precioNoche").value(330000))
                .andExpect(jsonPath("$.tipoAlojamiento").value("Casa"))
                .andExpect(jsonPath("$.servicios.length()").value(2))
                .andExpect(jsonPath("$.imagenes.length()").value(1))
                .andExpect(jsonPath("$.tarifaLimpieza").value(55000))
                .andExpect(jsonPath("$.serviciosAdicionales.length()").value(1));
    }
}
