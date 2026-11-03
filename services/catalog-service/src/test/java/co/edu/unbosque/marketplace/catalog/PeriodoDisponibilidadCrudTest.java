package co.edu.unbosque.marketplace.catalog;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import co.edu.unbosque.marketplace.catalog.client.BookingReservationClient;
import co.edu.unbosque.marketplace.catalog.domain.Alojamiento;
import co.edu.unbosque.marketplace.catalog.repository.AlojamientoRepository;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
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
    @MockBean private BookingReservationClient reservas;
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

    @Test
    void rechazaFechasAusentesIgualesOInvertidas() throws Exception {
        String ruta = "/api/v1/alojamientos/" + alojamientoId + "/periodos-disponibilidad";
        mockMvc.perform(post(ruta).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"fechaInicio\":null,\"fechaFin\":\"2026-12-05\"}"))
                .andExpect(status().isBadRequest());
        mockMvc.perform(post(ruta).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"fechaInicio\":\"2026-12-05\",\"fechaFin\":\"2026-12-05\"}"))
                .andExpect(status().isBadRequest());
        mockMvc.perform(post(ruta).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"fechaInicio\":\"2026-12-10\",\"fechaFin\":\"2026-12-05\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void rechazaCrearPeriodoSuperpuesto() throws Exception {
        String ruta = "/api/v1/alojamientos/" + alojamientoId + "/periodos-disponibilidad";
        mockMvc.perform(post(ruta).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"fechaInicio\":\"2026-12-01\",\"fechaFin\":\"2026-12-10\"}"))
                .andExpect(status().isCreated());
        mockMvc.perform(post(ruta).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"fechaInicio\":\"2026-12-10\",\"fechaFin\":\"2026-12-15\"}"))
                .andExpect(status().isConflict());
    }

    @Test
    void rechazaEditarCuandoGeneraSuperposicion() throws Exception {
        String ruta = "/api/v1/alojamientos/" + alojamientoId + "/periodos-disponibilidad";
        mockMvc.perform(post(ruta).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"fechaInicio\":\"2026-12-01\",\"fechaFin\":\"2026-12-05\"}"))
                .andExpect(status().isCreated());
        String segundo = mockMvc.perform(post(ruta).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"fechaInicio\":\"2026-12-10\",\"fechaFin\":\"2026-12-15\"}"))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        var mapper = com.fasterxml.jackson.databind.json.JsonMapper.builder().build();
        long segundoId = mapper.readTree(segundo).get("id").asLong();
        mockMvc.perform(put(ruta + "/" + segundoId).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"fechaInicio\":\"2026-12-04\",\"fechaFin\":\"2026-12-12\"}"))
                .andExpect(status().isConflict());
    }

    @Test
    void permitePeriodosSeparadosYNoConfundeAlojamientos() throws Exception {
        String ruta = "/api/v1/alojamientos/" + alojamientoId + "/periodos-disponibilidad";
        mockMvc.perform(post(ruta).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"fechaInicio\":\"2026-12-01\",\"fechaFin\":\"2026-12-05\"}"))
                .andExpect(status().isCreated());
        mockMvc.perform(post(ruta).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"fechaInicio\":\"2026-12-06\",\"fechaFin\":\"2026-12-10\"}"))
                .andExpect(status().isCreated());
    }

    @Test
    void impideEditarYEliminarUnPeriodoConReservaConfirmada() throws Exception {
        String ruta = "/api/v1/alojamientos/" + alojamientoId + "/periodos-disponibilidad";
        String respuesta = mockMvc.perform(post(ruta).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"fechaInicio\":\"2026-12-01\",\"fechaFin\":\"2026-12-10\"}"))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        long periodoId = com.fasterxml.jackson.databind.json.JsonMapper.builder().build()
                .readTree(respuesta).get("id").asLong();

        when(reservas.existeReservaConfirmada(eq(alojamientoId),
                any(LocalDate.class), any(LocalDate.class))).thenReturn(true);

        mockMvc.perform(put(ruta + "/" + periodoId).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"fechaInicio\":\"2026-12-12\",\"fechaFin\":\"2026-12-18\"}"))
                .andExpect(status().isConflict());

        mockMvc.perform(delete(ruta + "/" + periodoId))
                .andExpect(status().isConflict());
    }
}
