package co.edu.unbosque.marketplace.booking;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import co.edu.unbosque.marketplace.booking.domain.Reserva;
import co.edu.unbosque.marketplace.booking.repository.ReservaRepository;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:booking_overlap_test",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.flyway.enabled=false"
})
@AutoConfigureMockMvc
@Transactional
class ConfirmedReservationOverlapTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ReservaRepository reservas;

    @BeforeEach
    void limpiarReservas() {
        reservas.deleteAll();
    }

    @Test
    void informaCuandoExisteReservaConfirmadaQueSeCruzaConElPeriodo() throws Exception {
        reservas.save(new Reserva(10L, 25L,
                LocalDate.parse("2026-12-10"), LocalDate.parse("2026-12-15"), "CONFIRMADA"));

        mockMvc.perform(get("/internal/alojamientos/25/reservas-confirmadas/solapamiento")
                        .header("X-Internal-Secret", "dev-internal-secret-change-me")
                        .param("fechaInicio", "2026-12-01")
                        .param("fechaFin", "2026-12-20"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    void ignoraReservasNoConfirmadasYReservasDeOtrosAlojamientos() throws Exception {
        reservas.save(new Reserva(10L, 25L,
                LocalDate.parse("2026-12-10"), LocalDate.parse("2026-12-15"), "CANCELADA"));
        reservas.save(new Reserva(11L, 99L,
                LocalDate.parse("2026-12-10"), LocalDate.parse("2026-12-15"), "CONFIRMADA"));

        mockMvc.perform(get("/internal/alojamientos/25/reservas-confirmadas/solapamiento")
                        .header("X-Internal-Secret", "dev-internal-secret-change-me")
                        .param("fechaInicio", "2026-12-01")
                        .param("fechaFin", "2026-12-20"))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));
    }

    @Test
    void informaFalseCuandoLasFechasNoSeCruzan() throws Exception {
        reservas.save(new Reserva(10L, 25L,
                LocalDate.parse("2026-12-10"), LocalDate.parse("2026-12-15"), "CONFIRMADA"));

        mockMvc.perform(get("/internal/alojamientos/25/reservas-confirmadas/solapamiento")
                        .header("X-Internal-Secret", "dev-internal-secret-change-me")
                        .param("fechaInicio", "2026-12-20")
                        .param("fechaFin", "2026-12-25"))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));
    }
}
