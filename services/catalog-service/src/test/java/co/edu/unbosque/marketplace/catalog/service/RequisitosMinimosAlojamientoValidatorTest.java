package co.edu.unbosque.marketplace.catalog.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import co.edu.unbosque.marketplace.catalog.domain.Alojamiento;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;

class RequisitosMinimosAlojamientoValidatorTest {

    private final RequisitosMinimosAlojamientoValidator validator =
            new RequisitosMinimosAlojamientoValidator();

    @Test
    void aceptaAlojamientoConTodosLosRequisitosMinimos() {

        Alojamiento alojamiento = new Alojamiento(
                100L,
                "Apartamento de prueba",
                "Alojamiento para vacaciones",
                "Calle 10",
                "Bogota",
                new BigDecimal("150000"),
                4
        );

        assertTrue(validator.cumpleRequisitosMinimos(alojamiento));
        assertTrue(validator.obtenerCamposFaltantes(alojamiento).isEmpty());
    }

    @Test
    void identificaTodosLosCamposFaltantes() {

        Alojamiento alojamiento = new Alojamiento(
                100L,
                "",
                null,
                "",
                null,
                null,
                null
        );

        List<String> camposEsperados = List.of(
                "titulo",
                "descripcion",
                "direccion",
                "ciudad",
                "precioNoche",
                "capacidad"
        );

        assertEquals(
                camposEsperados,
                validator.obtenerCamposFaltantes(alojamiento)
        );

        assertFalse(validator.cumpleRequisitosMinimos(alojamiento));
    }

    @Test
    void identificaSolamenteLosCamposQueFaltan() {

        Alojamiento alojamiento = new Alojamiento(
                100L,
                "Apartamento de prueba",
                "",
                "Calle 10",
                "Bogota",
                new BigDecimal("150000"),
                4
        );

        assertEquals(
                List.of("descripcion"),
                validator.obtenerCamposFaltantes(alojamiento)
        );
    }

    @Test
    void rechazaPrecioYCapacidadInvalidos() {

        Alojamiento alojamiento = new Alojamiento(
                100L,
                "Apartamento de prueba",
                "Alojamiento para vacaciones",
                "Calle 10",
                "Bogota",
                BigDecimal.ZERO,
                0
        );

        assertEquals(
                List.of("precioNoche", "capacidad"),
                validator.obtenerCamposFaltantes(alojamiento)
        );

        assertFalse(validator.cumpleRequisitosMinimos(alojamiento));
    }
}
