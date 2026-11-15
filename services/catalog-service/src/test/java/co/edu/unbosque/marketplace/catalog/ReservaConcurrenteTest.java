package co.edu.unbosque.marketplace.catalog;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import co.edu.unbosque.marketplace.catalog.domain.Alojamiento;
import co.edu.unbosque.marketplace.catalog.domain.PeriodoDisponibilidad;
import co.edu.unbosque.marketplace.catalog.repository.AlojamientoRepository;
import co.edu.unbosque.marketplace.catalog.repository.PeriodoDisponibilidadRepository;
import java.time.LocalDate;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:catalog_concurrency_test",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.flyway.enabled=false",
        "app.internal.secret=secret-test-catalog"
})
@AutoConfigureMockMvc
class ReservaConcurrenteTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AlojamientoRepository alojamientos;

    @Autowired
    private PeriodoDisponibilidadRepository periodos;

    private Long alojamientoId;

    @BeforeEach
    void preparar() {
        Alojamiento alojamiento = alojamientos.save(
                new Alojamiento(700L, "Alojamiento concurrente", "ACTIVA"));

        alojamientoId = alojamiento.getId();

        periodos.save(new PeriodoDisponibilidad(
                alojamiento,
                LocalDate.of(2027, 1, 1),
                LocalDate.of(2027, 1, 10)));
    }

    @Test
    void dosSolicitudesSimultaneasSoloPermitenUnaReserva() throws Exception {

        String ruta = "/internal/alojamientos/" + alojamientoId
                + "/periodos-disponibilidad/reservar";

        CountDownLatch preparadas = new CountDownLatch(2);
        CountDownLatch inicio = new CountDownLatch(1);

        ExecutorService executor = Executors.newFixedThreadPool(2);

        try {
            Future<Integer> primera = executor.submit(() -> {
                preparadas.countDown();
                inicio.await();

                MvcResult resultado = mockMvc.perform(post(ruta)
                                .header("X-Internal-Secret", "secret-test-catalog")
                                .param("reservaId", "9501")
                                .param("fechaInicio", "2027-01-02")
                                .param("fechaFin", "2027-01-05"))
                        .andReturn();

                return resultado.getResponse().getStatus();
            });

            Future<Integer> segunda = executor.submit(() -> {
                preparadas.countDown();
                inicio.await();

                MvcResult resultado = mockMvc.perform(post(ruta)
                                .header("X-Internal-Secret", "secret-test-catalog")
                                .param("reservaId", "9502")
                                .param("fechaInicio", "2027-01-02")
                                .param("fechaFin", "2027-01-05"))
                        .andReturn();

                return resultado.getResponse().getStatus();
            });

            boolean ambasPreparadas = preparadas.await(10, TimeUnit.SECONDS);

            assertEquals(true, ambasPreparadas,
                    "Las dos solicitudes deben estar preparadas para iniciar");

            inicio.countDown();

            int estadoPrimera = primera.get(20, TimeUnit.SECONDS);
            int estadoSegunda = segunda.get(20, TimeUnit.SECONDS);

            int exitosas = (estadoPrimera == HttpStatus.NO_CONTENT.value() ? 1 : 0)
                    + (estadoSegunda == HttpStatus.NO_CONTENT.value() ? 1 : 0);

            int conflictos = (estadoPrimera == HttpStatus.CONFLICT.value() ? 1 : 0)
                    + (estadoSegunda == HttpStatus.CONFLICT.value() ? 1 : 0);

            assertEquals(1, exitosas,
                    "Solo una solicitud debe reservar el periodo");

            assertEquals(1, conflictos,
                    "La otra solicitud debe recibir HTTP 409");

        } finally {
            inicio.countDown();
            executor.shutdownNow();
        }
    }
}