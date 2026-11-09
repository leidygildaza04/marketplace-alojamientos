package co.edu.unbosque.marketplace.catalog.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import co.edu.unbosque.marketplace.catalog.client.AuditClient;
import co.edu.unbosque.marketplace.catalog.client.BookingReviewClient;
import co.edu.unbosque.marketplace.catalog.client.NotificationClient;
import co.edu.unbosque.marketplace.catalog.domain.Alojamiento;
import co.edu.unbosque.marketplace.catalog.repository.AlojamientoRepository;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class AlojamientoServiceAuditTest {

    @Mock
    private AlojamientoRepository repository;

    @Mock
    private AuditClient auditClient;

    @Mock
    private BookingReviewClient bookingReviewClient;

    @Mock
    private NotificationClient notificationClient;

    private AlojamientoService service;

    @BeforeEach
    void setUp() {
        service = new AlojamientoService(
                repository,
                new RequisitosMinimosAlojamientoValidator(),
                auditClient,
                bookingReviewClient,
                notificationClient
        );
    }

    @Test
    void registraAuditoriaCuandoLaActivacionEsExitosa() {
        Alojamiento alojamiento = alojamientoCompleto();
        ReflectionTestUtils.setField(alojamiento, "id", 15L);

        when(repository.findById(15L)).thenReturn(Optional.of(alojamiento));
        when(repository.saveAndFlush(alojamiento)).thenReturn(alojamiento);

        Alojamiento resultado = service.activar(7L, false, 15L);

        assertEquals("ACTIVA", resultado.getEstado());
        verify(auditClient).registrarActivacion(7L, 15L);
    }

    @Test
    void noRegistraAuditoriaSiFaltanRequisitos() {
        Alojamiento alojamiento = new Alojamiento(7L, "Apartamento", "BORRADOR");
        when(repository.findById(15L)).thenReturn(Optional.of(alojamiento));

        assertThrows(ResponseStatusException.class,
                () -> service.activar(7L, false, 15L));

        verify(auditClient, never()).registrarActivacion(7L, 15L);
    }

    private Alojamiento alojamientoCompleto() {
        return new Alojamiento(
                7L,
                "Apartamento",
                "Apartamento completo",
                "Calle 1 # 2-3",
                "Bogotá",
                new BigDecimal("150000"),
                3
        );
    }
}
