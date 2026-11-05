
package co.edu.unbosque.marketplace.catalog.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AlojamientoEstadoTransicionTest {

    @Test
    void debePermitirTransicionDeBorradorAActiva() {
        Alojamiento alojamiento =
                new Alojamiento(1L, "Apartamento", "BORRADOR");

        alojamiento.cambiarEstadoA("ACTIVA");

        assertEquals("ACTIVA", alojamiento.getEstado());
    }

    @Test
    void debePermitirTransicionDeActivaAPausada() {
        Alojamiento alojamiento =
                new Alojamiento(1L, "Apartamento", "ACTIVA");

        alojamiento.cambiarEstadoA("PAUSADA");

        assertEquals("PAUSADA", alojamiento.getEstado());
    }

    @Test
    void debePermitirTransicionDePausadaAActiva() {
        Alojamiento alojamiento =
                new Alojamiento(1L, "Apartamento", "PAUSADA");

        alojamiento.cambiarEstadoA("ACTIVA");

        assertEquals("ACTIVA", alojamiento.getEstado());
    }

    @Test
    void debeRechazarTransicionDeBorradorAPausada() {
        Alojamiento alojamiento =
                new Alojamiento(1L, "Apartamento", "BORRADOR");

        assertThrows(
                IllegalStateException.class,
                () -> alojamiento.cambiarEstadoA("PAUSADA")
        );

        assertEquals("BORRADOR", alojamiento.getEstado());
    }

    @Test
    void debeRechazarTransicionDeActivaABorrador() {
        Alojamiento alojamiento =
                new Alojamiento(1L, "Apartamento", "ACTIVA");

        assertThrows(
                IllegalStateException.class,
                () -> alojamiento.cambiarEstadoA("BORRADOR")
        );

        assertEquals("ACTIVA", alojamiento.getEstado());
    }

    @Test
    void debeRechazarTransicionDePausadaABorrador() {
        Alojamiento alojamiento =
                new Alojamiento(1L, "Apartamento", "PAUSADA");

        assertThrows(
                IllegalStateException.class,
                () -> alojamiento.cambiarEstadoA("BORRADOR")
        );

        assertEquals("PAUSADA", alojamiento.getEstado());
    }

    @Test
    void debeRechazarUnEstadoNoReconocido() {
        Alojamiento alojamiento =
                new Alojamiento(1L, "Apartamento", "BORRADOR");

        assertThrows(
                IllegalArgumentException.class,
                () -> alojamiento.cambiarEstadoA("PUBLICADA")
        );

        assertEquals("BORRADOR", alojamiento.getEstado());
    }

    @Test
    void debeRechazarUnEstadoNulo() {
        Alojamiento alojamiento =
                new Alojamiento(1L, "Apartamento", "BORRADOR");

        assertThrows(
                IllegalArgumentException.class,
                () -> alojamiento.cambiarEstadoA(null)
        );

        assertEquals("BORRADOR", alojamiento.getEstado());
    }
}
