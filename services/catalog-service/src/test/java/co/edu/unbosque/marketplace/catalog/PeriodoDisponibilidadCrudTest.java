package co.edu.unbosque.marketplace.catalog;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import co.edu.unbosque.marketplace.catalog.domain.Alojamiento;
import co.edu.unbosque.marketplace.catalog.repository.AlojamientoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:catalog_crud_test",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.flyway.enabled=false"
})
@AutoConfigureMockMvc
@Transactional
class PeriodoDisponibilidadCrudTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private AlojamientoRepository alojamientos;
    private Long alojamientoId;

    @BeforeEach
    void prepararAlojamiento() {
        Alojamiento alojamiento = new Alojamiento(100L, "Apartamento de prueba", "ACTIVA");
        alojamientoId = alojamientos.save(alojamiento).getId();
    }

    @Test
    void permiteCrearConsultarEditarYEliminarPeriodo() throws Exception {
        String ruta = "/api/v1/alojamientos/" + alojamientoId + "/periodos-disponibilidad";
        String cuerpoInicial = "{\"fechaInicio\":\"2026-12-01\",\"fechaFin\":\"2026-12-05\"}";

        String respuesta = mockMvc.perform(post(ruta)
                        .contentType(MediaType.APPLICATION_JSON).content(cuerpoInicial))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.alojamientoId").value(alojamientoId))
                .andExpect(jsonPath("$.estado").value("DISPONIBLE"))
                .andReturn().getResponse().getContentAsString();

        long periodoId = com.fasterxml.jackson.databind.json.JsonMapper.builder().build()
                .readTree(respuesta).get("id").asLong();

        mockMvc.perform(get(ruta))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(periodoId));

        mockMvc.perform(put(ruta + "/" + periodoId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"fechaInicio\":\"2026-12-10\",\"fechaFin\":\"2026-12-15\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fechaInicio").value("2026-12-10"));

        mockMvc.perform(delete(ruta + "/" + periodoId))
                .andExpect(status().isNoContent());

        mockMvc.perform(get(ruta))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void devuelve404CuandoElAlojamientoNoExiste() throws Exception {
        mockMvc.perform(get("/api/v1/alojamientos/999999/periodos-disponibilidad"))
                .andExpect(status().isNotFound());
    }
}
