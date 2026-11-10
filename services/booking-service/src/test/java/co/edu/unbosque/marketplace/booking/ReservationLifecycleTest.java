
package co.edu.unbosque.marketplace.booking;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import co.edu.unbosque.marketplace.booking.client.CatalogPeriodClient;
import co.edu.unbosque.marketplace.booking.domain.Reserva;
import co.edu.unbosque.marketplace.booking.repository.ReservaRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:booking_lifecycle_test",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.flyway.enabled=false"
})
@AutoConfigureMockMvc
class ReservationLifecycleTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ReservaRepository reservas;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CatalogPeriodClient catalog;

    @Test
    void confirmaReservaDespuesDeBloquearPeriodo() throws Exception {

        doNothing().when(catalog).reservar(
                eq(44L),
                any(Long.class),
                eq(LocalDate.of(2027, 2, 1)),
                eq(LocalDate.of(2027, 2, 4)));

        mockMvc.perform(post("/api/v1/reservas")
                        .header("Idempotency-Key", "confirmacion-001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\":5,\"alojamientoId\":44,"
                                + "\"checkIn\":\"2027-02-01\","
                                + "\"checkOut\":\"2027-02-04\","
                                + "\"resultadoPago\":\"APROBADO\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.estado").value("CONFIRMADA"));

        verify(catalog).reservar(
                eq(44L),
                any(Long.class),
                eq(LocalDate.of(2027, 2, 1)),
                eq(LocalDate.of(2027, 2, 4)));
    }

    @Test
    void devuelveLaReservaOriginalCuandoSeRepiteLaMismaClave() throws Exception {

        doNothing().when(catalog).reservar(
                eq(44L),
                any(Long.class),
                eq(LocalDate.of(2027, 2, 1)),
                eq(LocalDate.of(2027, 2, 4)));

        String contenido = "{\"userId\":5,\"alojamientoId\":44,"
                + "\"checkIn\":\"2027-02-01\","
                + "\"checkOut\":\"2027-02-04\","
                + "\"resultadoPago\":\"APROBADO\"}";

        MvcResult primeraRespuesta = mockMvc.perform(post("/api/v1/reservas")
                        .header("Idempotency-Key", "reintento-001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(contenido))
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode primeraReserva = objectMapper.readTree(
                primeraRespuesta.getResponse().getContentAsString());

        MvcResult segundaRespuesta = mockMvc.perform(post("/api/v1/reservas")
                        .header("Idempotency-Key", "reintento-001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(contenido))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.estado").value("CONFIRMADA"))
                .andReturn();

        JsonNode segundaReserva = objectMapper.readTree(
                segundaRespuesta.getResponse().getContentAsString());

        org.junit.jupiter.api.Assertions.assertEquals(
                primeraReserva.get("id").asLong(),
                segundaReserva.get("id").asLong());

        verify(catalog, times(1)).reservar(
                eq(44L),
                any(Long.class),
                eq(LocalDate.of(2027, 2, 1)),
                eq(LocalDate.of(2027, 2, 4)));
    }

    @Test
    void rechazaLaMismaClaveConDatosDiferentes() throws Exception {

        doNothing().when(catalog).reservar(
                eq(44L),
                any(Long.class),
                eq(LocalDate.of(2027, 2, 1)),
                eq(LocalDate.of(2027, 2, 4)));

        mockMvc.perform(post("/api/v1/reservas")
                        .header("Idempotency-Key", "clave-conflicto-001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\":5,\"alojamientoId\":44,"
                                + "\"checkIn\":\"2027-02-01\","
                                + "\"checkOut\":\"2027-02-04\","
                                + "\"resultadoPago\":\"APROBADO\"}"))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/v1/reservas")
                        .header("Idempotency-Key", "clave-conflicto-001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\":5,\"alojamientoId\":99,"
                                + "\"checkIn\":\"2027-03-01\","
                                + "\"checkOut\":\"2027-03-04\","
                                + "\"resultadoPago\":\"APROBADO\"}"))
                .andExpect(status().isConflict());

        verify(catalog, times(1)).reservar(
                eq(44L),
                any(Long.class),
                eq(LocalDate.of(2027, 2, 1)),
                eq(LocalDate.of(2027, 2, 4)));
    }

    @Test
    void cancelacionLiberaPeriodoYMarcaReservaCancelada() throws Exception {

        Reserva reserva = reservas.save(new Reserva(
                5L,
                44L,
                LocalDate.of(2027, 2, 1),
                LocalDate.of(2027, 2, 4),
                "CONFIRMADA"));

        doNothing().when(catalog).liberar(44L, reserva.getId());

        mockMvc.perform(post(
                        "/api/v1/reservas/" + reserva.getId() + "/cancelar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("CANCELADA"));

        verify(catalog).liberar(44L, reserva.getId());
    }
}
