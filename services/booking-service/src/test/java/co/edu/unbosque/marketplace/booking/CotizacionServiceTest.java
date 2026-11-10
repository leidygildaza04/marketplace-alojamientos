
package co.edu.unbosque.marketplace.booking;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import co.edu.unbosque.marketplace.booking.client.AlojamientoPreciosResponse;
import co.edu.unbosque.marketplace.booking.client.CatalogPricingClient;
import co.edu.unbosque.marketplace.booking.dto.CrearCotizacionRequest;
import co.edu.unbosque.marketplace.booking.dto.CotizacionResponse;
import co.edu.unbosque.marketplace.booking.service.CotizacionService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class CotizacionServiceTest {

    @Mock
    private CatalogPricingClient catalog;

    private CotizacionService cotizaciones;

    @BeforeEach
    void configurarServicio() {
        cotizaciones = new CotizacionService(
                catalog,
                new BigDecimal("10"),
                BigDecimal.ZERO);
    }

    @Test
    void calculaNochesLimpiezaServiciosYTarifaDeServicio() {

        when(catalog.consultarPrecios(44L)).thenReturn(
                new AlojamientoPreciosResponse(
                        44L,
                        new BigDecimal("100000.00"),
                        new BigDecimal("30000.00"),
                        List.of(
                                new AlojamientoPreciosResponse.ServicioAdicionalPrecioResponse(
                                        1L,
                                        "Desayuno",
                                        new BigDecimal("20000.00")),
                                new AlojamientoPreciosResponse.ServicioAdicionalPrecioResponse(
                                        2L,
                                        "Parqueadero",
                                        new BigDecimal("15000.00")))));

        CrearCotizacionRequest request = new CrearCotizacionRequest(
                44L,
                LocalDate.of(2027, 2, 1),
                LocalDate.of(2027, 2, 4),
                List.of(1L));

        CotizacionResponse resultado = cotizaciones.calcular(request);

        assertEquals(3L, resultado.noches());
        assertEquals(
                new BigDecimal("100000.00"),
                resultado.precioPorNoche());
        assertEquals(
                new BigDecimal("300000.00"),
                resultado.subtotalAlojamiento());
        assertEquals(
                new BigDecimal("30000.00"),
                resultado.tarifaLimpieza());
        assertEquals(
                new BigDecimal("20000.00"),
                resultado.totalServiciosAdicionales());
        assertEquals(
                new BigDecimal("10"),
                resultado.porcentajeServicio());
        assertEquals(
                new BigDecimal("35000.00"),
                resultado.tarifaServicio());
        assertEquals(
                BigDecimal.ZERO,
                resultado.porcentajeDescuento());
        assertEquals(
                new BigDecimal("0.00"),
                resultado.descuento());
        assertEquals(
                new BigDecimal("385000.00"),
                resultado.total());
        assertEquals(1, resultado.serviciosAdicionales().size());

        verify(catalog).consultarPrecios(44L);
    }

    @Test
    void aplicaPorcentajeDeDescuentoConfigurable() {

        cotizaciones = new CotizacionService(
                catalog,
                new BigDecimal("10"),
                new BigDecimal("5"));

        when(catalog.consultarPrecios(44L)).thenReturn(
                new AlojamientoPreciosResponse(
                        44L,
                        new BigDecimal("100000.00"),
                        new BigDecimal("30000.00"),
                        List.of(
                                new AlojamientoPreciosResponse.ServicioAdicionalPrecioResponse(
                                        1L,
                                        "Desayuno",
                                        new BigDecimal("20000.00")))));

        CrearCotizacionRequest request = new CrearCotizacionRequest(
                44L,
                LocalDate.of(2027, 2, 1),
                LocalDate.of(2027, 2, 4),
                List.of(1L));

        CotizacionResponse resultado = cotizaciones.calcular(request);

        assertEquals(
                new BigDecimal("35000.00"),
                resultado.tarifaServicio());
        assertEquals(
                new BigDecimal("5"),
                resultado.porcentajeDescuento());
        assertEquals(
                new BigDecimal("17500.00"),
                resultado.descuento());
        assertEquals(
                new BigDecimal("367500.00"),
                resultado.total());
    }

    @Test
    void rechazaPorcentajeDeServicioMayorA100() {

        assertThrows(
                IllegalArgumentException.class,
                () -> new CotizacionService(
                        catalog,
                        new BigDecimal("101"),
                        BigDecimal.ZERO));

        verifyNoInteractions(catalog);
    }

    @Test
    void rechazaPorcentajeDeDescuentoNegativo() {

        assertThrows(
                IllegalArgumentException.class,
                () -> new CotizacionService(
                        catalog,
                        new BigDecimal("10"),
                        new BigDecimal("-1")));

        verifyNoInteractions(catalog);
    }

    @Test
    void rechazaFechasCuandoSalidaNoEsPosteriorAEntrada() {

        CrearCotizacionRequest request = new CrearCotizacionRequest(
                44L,
                LocalDate.of(2027, 2, 4),
                LocalDate.of(2027, 2, 4),
                List.of());

        assertThrows(
                ResponseStatusException.class,
                () -> cotizaciones.calcular(request));

        verifyNoInteractions(catalog);
    }

    @Test
    void rechazaServicioAdicionalQueNoPerteneceAlAlojamiento() {

        when(catalog.consultarPrecios(44L)).thenReturn(
                new AlojamientoPreciosResponse(
                        44L,
                        new BigDecimal("100000.00"),
                        BigDecimal.ZERO,
                        List.of()));

        CrearCotizacionRequest request = new CrearCotizacionRequest(
                44L,
                LocalDate.of(2027, 2, 1),
                LocalDate.of(2027, 2, 4),
                List.of(99L));

        assertThrows(
                ResponseStatusException.class,
                () -> cotizaciones.calcular(request));
    }
}
