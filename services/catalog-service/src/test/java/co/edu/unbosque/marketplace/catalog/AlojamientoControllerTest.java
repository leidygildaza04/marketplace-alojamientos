
package co.edu.unbosque.marketplace.catalog;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.test.context.support.WithMockUser;

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

}
