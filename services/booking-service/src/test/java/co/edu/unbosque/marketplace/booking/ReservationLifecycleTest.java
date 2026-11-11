package co.edu.unbosque.marketplace.booking;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import co.edu.unbosque.marketplace.booking.client.CatalogPeriodClient;
import co.edu.unbosque.marketplace.booking.client.CatalogListingsClient;
import co.edu.unbosque.marketplace.booking.client.CatalogAccommodationClient;
import co.edu.unbosque.marketplace.booking.client.CatalogListingsResponse;
import co.edu.unbosque.marketplace.booking.client.CatalogAccommodationSummaryResponse;
import co.edu.unbosque.marketplace.booking.dto.CrearCotizacionRequest;
import co.edu.unbosque.marketplace.booking.dto.CotizacionResponse;
import co.edu.unbosque.marketplace.booking.service.CotizacionService;
import java.math.BigDecimal;
import java.util.List;
import static org.mockito.Mockito.when;
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

    @MockBean
    private CatalogListingsClient catalogListings;

    @MockBean
    private CatalogAccommodationClient catalogAccommodation;
    @MockBean
    private CotizacionService cotizaciones;

    private void configurarCotizacionDePrueba() {
        when(cotizaciones.calcular(any(CrearCotizacionRequest.class)))
                .thenReturn(new CotizacionResponse(
                        44L,
                        LocalDate.of(2027, 2, 1),
                        LocalDate.of(2027, 2, 4),
                        3L,
                        new BigDecimal("100000.00"),
                        new BigDecimal("300000.00"),
                        BigDecimal.ZERO,
                        List.of(),
                        BigDecimal.ZERO,
                        BigDecimal.ZERO,
                        BigDecimal.ZERO,
                        BigDecimal.ZERO,
                        BigDecimal.ZERO,
                        new BigDecimal("300000.00")
                ));
    }
    @Test
    void confirmaReservaDespuesDeBloquearPeriodo() throws Exception {
        configurarCotizacionDePrueba();

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

        Reserva guardada = reservas.findByUserIdAndClaveIdempotencia(
                5L,
                "confirmacion-001"
        ).orElseThrow();

        assertEquals(
        0,
        new BigDecimal("300000.00").compareTo(guardada.getTotal())
);

        JsonNode cotizacionGuardada = objectMapper.readTree(
                guardada.getCotizacionSnapshot()
        );

        assertEquals(
        0,
        new BigDecimal("300000.00").compareTo(
                cotizacionGuardada.get("total").decimalValue()
        )
);
        verify(catalog).reservar(
                eq(44L),
                any(Long.class),
                eq(LocalDate.of(2027, 2, 1)),
                eq(LocalDate.of(2027, 2, 4)));
    }

    @Test
    void devuelveLaReservaOriginalCuandoSeRepiteLaMismaClave() throws Exception {
        configurarCotizacionDePrueba();

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

        assertEquals(
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
        configurarCotizacionDePrueba();

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

    @Test
    void rechazaReservaCuandoElPagoEsRechazadoYLiberarElPeriodo() throws Exception {
        configurarCotizacionDePrueba();

        doNothing().when(catalog).reservar(
                eq(44L),
                any(Long.class),
                eq(LocalDate.of(2027, 2, 1)),
                eq(LocalDate.of(2027, 2, 4)));

        doNothing().when(catalog).liberar(
                eq(44L),
                any(Long.class));

        mockMvc.perform(post("/api/v1/reservas")
                        .header("Idempotency-Key", "pago-rechazado-001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\":5,\"alojamientoId\":44,"
                                + "\"checkIn\":\"2027-02-01\","
                                + "\"checkOut\":\"2027-02-04\","
                                + "\"resultadoPago\":\"RECHAZADO\"}"))
                .andExpect(status().is(402));

        Reserva reserva = reservas.findByUserIdAndClaveIdempotencia(
                5L,
                "pago-rechazado-001"
        ).orElseThrow();

        assertEquals("RECHAZADA", reserva.getEstado());

        verify(catalog).reservar(
                eq(44L),
                any(Long.class),
                eq(LocalDate.of(2027, 2, 1)),
                eq(LocalDate.of(2027, 2, 4)));

        verify(catalog).liberar(44L, reserva.getId());
    }

    @Test
    void consultaHistorialDeReservasDelHuesped() throws Exception {
        Reserva reserva = reservas.save(new Reserva(
                701L,
                44L,
                LocalDate.of(2027, 2, 1),
                LocalDate.of(2027, 2, 4),
                "CONFIRMADA"));

        when(catalogAccommodation.consultarResumen(44L))
                .thenReturn(new CatalogAccommodationSummaryResponse(
                        44L, "Apartamento de prueba", "Bogota", "Calle 10"));

        mockMvc.perform(get("/api/v1/reservas/huesped/701"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(reserva.getId()))
                .andExpect(jsonPath("$[0].userId").value(701))
                .andExpect(jsonPath("$[0].alojamientoId").value(44))
                .andExpect(jsonPath("$[0].tituloAlojamiento")
                        .value("Apartamento de prueba"))
                .andExpect(jsonPath("$[0].estado").value("CONFIRMADA"))
                .andExpect(jsonPath("$[0].total").doesNotExist());
    }

    @Test
    void consultaHistorialDelAnfitrionSoloConSusAlojamientos()
            throws Exception {
        Reserva reservaPropia = reservas.save(new Reserva(
                801L,
                44L,
                LocalDate.of(2027, 3, 1),
                LocalDate.of(2027, 3, 4),
                "CONFIRMADA"));

        reservas.save(new Reserva(
                802L,
                99L,
                LocalDate.of(2027, 3, 5),
                LocalDate.of(2027, 3, 8),
                "CONFIRMADA"));

        when(catalogListings.consultarAlojamientos(90L))
                .thenReturn(new CatalogListingsResponse(
                        90L, List.of(44L), 1));

        when(catalogAccommodation.consultarResumen(44L))
                .thenReturn(new CatalogAccommodationSummaryResponse(
                        44L, "Alojamiento del anfitrion", "Bogota", "Carrera 15"));

        mockMvc.perform(get("/api/v1/reservas/anfitrion/90"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(reservaPropia.getId()))
                .andExpect(jsonPath("$[0].alojamientoId").value(44))
                .andExpect(jsonPath("$[0].userId").value(801));

        verify(catalogListings).consultarAlojamientos(90L);
    }

    @Test
    void consultaDetalleConCotizacionGuardadaEnLaReserva()
            throws Exception {
        CotizacionResponse cotizacion = new CotizacionResponse(
                44L,
                LocalDate.of(2027, 4, 1),
                LocalDate.of(2027, 4, 4),
                3L,
                new BigDecimal("100000.00"),
                new BigDecimal("300000.00"),
                BigDecimal.ZERO,
                List.of(),
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                new BigDecimal("300000.00"));

        Reserva reserva = new Reserva(
                901L,
                44L,
                LocalDate.of(2027, 4, 1),
                LocalDate.of(2027, 4, 4),
                "CONFIRMADA");

        reserva.guardarCotizacion(
                cotizacion.total(),
                objectMapper.writeValueAsString(cotizacion));

        reserva = reservas.save(reserva);

        when(catalogAccommodation.consultarResumen(44L))
                .thenReturn(new CatalogAccommodationSummaryResponse(
                        44L, "Casa de prueba", "Medellin", "Calle 20"));

        mockMvc.perform(get("/api/v1/reservas/" + reserva.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(reserva.getId()))
                .andExpect(jsonPath("$.tituloAlojamiento").value("Casa de prueba"))
                .andExpect(jsonPath("$.ciudad").value("Medellin"))
                .andExpect(jsonPath("$.estado").value("CONFIRMADA"))
                .andExpect(jsonPath("$.total").value(300000.00))
                .andExpect(jsonPath("$.cotizacion.total").value(300000.00));
    }
}