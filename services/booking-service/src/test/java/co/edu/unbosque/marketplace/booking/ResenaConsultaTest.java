package co.edu.unbosque.marketplace.booking;

import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import co.edu.unbosque.marketplace.booking.domain.Resena;
import co.edu.unbosque.marketplace.booking.repository.ResenaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:booking_resenas_test",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.flyway.enabled=false",
        "app.jwt.secret=test-jwt-secret-test-jwt-secret-1234567890",
        "app.internal.secret=dev-internal-secret-change-me"
})
@AutoConfigureMockMvc
@Transactional
class ResenaConsultaTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ResenaRepository resenas;

    @BeforeEach
    void limpiarResenas() {
        resenas.deleteAll();
    }

    @Test
    void consultaPromedioYSoloIncluyeResenasVisibles() throws Exception {
        resenas.save(new Resena(1L, 10L, 25L, 5, "Excelente alojamiento"));
        resenas.save(new Resena(2L, 11L, 25L, 3, "Buena experiencia"));

        Resena oculta = new Resena(3L, 12L, 25L, 1, "Comentario no visible");
        ReflectionTestUtils.setField(oculta, "visible", false);
        resenas.save(oculta);

        mockMvc.perform(get("/internal/alojamientos/25/resenas")
                        .header("X-Internal-Secret", "dev-internal-secret-change-me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.promedio").value(4.0))
                .andExpect(jsonPath("$.cantidad").value(2))
                .andExpect(jsonPath("$.resenas.length()").value(2));
    }

    @Test
    void devuelveResumenVacioCuandoNoHayResenas() throws Exception {
        mockMvc.perform(get("/internal/alojamientos/25/resenas")
                        .header("X-Internal-Secret", "dev-internal-secret-change-me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.promedio").value(nullValue()))
                .andExpect(jsonPath("$.cantidad").value(0))
                .andExpect(jsonPath("$.resenas.length()").value(0));
    }
}