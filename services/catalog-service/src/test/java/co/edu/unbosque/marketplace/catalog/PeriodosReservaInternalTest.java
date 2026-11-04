package co.edu.unbosque.marketplace.catalog;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.transaction.annotation.Transactional;

import co.edu.unbosque.marketplace.catalog.domain.Alojamiento;
import co.edu.unbosque.marketplace.catalog.domain.PeriodoDisponibilidad;
import co.edu.unbosque.marketplace.catalog.repository.AlojamientoRepository;
import co.edu.unbosque.marketplace.catalog.repository.PeriodoDisponibilidadRepository;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:catalog_reservations_test",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.flyway.enabled=false",
        "app.internal.secret=secret-test-catalog"
})
@AutoConfigureMockMvc
@Transactional
class PeriodosReservaInternalTest {

    private static final String INTERNAL_SECRET = "secret-test-catalog";

    @Autowired private MockMvc mockMvc;
    @Autowired private AlojamientoRepository alojamientos;
    @Autowired private PeriodoDisponibilidadRepository periodos;
    private Long alojamientoId;

    @BeforeEach
    void preparar() {
        Alojamiento alojamiento = alojamientos.save(
                new Alojamiento(700L, "Alojamiento reserva", "ACTIVA"));
        alojamientoId = alojamiento.getId();
        periodos.save(new PeriodoDisponibilidad(alojamiento,
                LocalDate.of(2027, 1, 1), LocalDate.of(2027, 1, 10)));
    }

    @Test
    void primeraReservaBloqueaPeriodoYLaSegundaRecibe409() throws Exception {
        String ruta = "/internal/alojamientos/" + alojamientoId
                + "/periodos-disponibilidad/reservar";

        mockMvc.perform(post(ruta)
                        .header("X-Internal-Secret", INTERNAL_SECRET)
                        .param("reservaId", "9001")
                        .param("fechaInicio", "2027-01-02")
                        .param("fechaFin", "2027-01-05"))
                .andExpect(status().isNoContent());

        mockMvc.perform(post(ruta)
                        .header("X-Internal-Secret", INTERNAL_SECRET)
                        .param("reservaId", "9002")
                        .param("fechaInicio", "2027-01-02")
                        .param("fechaFin", "2027-01-05"))
                .andExpect(status().isConflict());
    }

    @Test
    void liberarSoloElIdDeReservaPropietarioPermiteReservarDeNuevo() throws Exception {
        String base = "/internal/alojamientos/" + alojamientoId
                + "/periodos-disponibilidad";

        mockMvc.perform(post(base + "/reservar")
                        .header("X-Internal-Secret", INTERNAL_SECRET)
                        .param("reservaId", "9101")
                        .param("fechaInicio", "2027-01-02")
                        .param("fechaFin", "2027-01-05"))
                .andExpect(status().isNoContent());

        mockMvc.perform(post(base + "/liberar")
                        .header("X-Internal-Secret", INTERNAL_SECRET)
                        .param("reservaId", "9999"))
                .andExpect(status().isNoContent());

        mockMvc.perform(post(base + "/reservar")
                        .header("X-Internal-Secret", INTERNAL_SECRET)
                        .param("reservaId", "9102")
                        .param("fechaInicio", "2027-01-02")
                        .param("fechaFin", "2027-01-05"))
                .andExpect(status().isConflict());

        mockMvc.perform(post(base + "/liberar")
                        .header("X-Internal-Secret", INTERNAL_SECRET)
                        .param("reservaId", "9101"))
                .andExpect(status().isNoContent());

        mockMvc.perform(post(base + "/reservar")
                        .header("X-Internal-Secret", INTERNAL_SECRET)
                        .param("reservaId", "9102")
                        .param("fechaInicio", "2027-01-02")
                        .param("fechaFin", "2027-01-05"))
                .andExpect(status().isNoContent());
    }

    @Test
    void rechazaSolicitudInternaSinSecreto() throws Exception {
        mockMvc.perform(post("/internal/alojamientos/" + alojamientoId
                        + "/periodos-disponibilidad/reservar")
                        .param("reservaId", "9201")
                        .param("fechaInicio", "2027-01-02")
                        .param("fechaFin", "2027-01-05"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void rechazaSolicitudInternaConSecretoIncorrecto() throws Exception {
        mockMvc.perform(post("/internal/alojamientos/" + alojamientoId
                        + "/periodos-disponibilidad/reservar")
                        .header("X-Internal-Secret", "secreto-incorrecto")
                        .param("reservaId", "9202")
                        .param("fechaInicio", "2027-01-02")
                        .param("fechaFin", "2027-01-05"))
                .andExpect(status().isUnauthorized());
    }
}