package co.edu.unbosque.marketplace.catalog.service;

import co.edu.unbosque.marketplace.catalog.domain.Alojamiento;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class RequisitosMinimosAlojamientoValidator {

    public List<String> obtenerCamposFaltantes(Alojamiento alojamiento) {

        List<String> camposFaltantes = new ArrayList<>();

        if (alojamiento.getTitulo() == null
                || alojamiento.getTitulo().isBlank()) {
            camposFaltantes.add("titulo");
        }

        if (alojamiento.getDescripcion() == null
                || alojamiento.getDescripcion().isBlank()) {
            camposFaltantes.add("descripcion");
        }

        if (alojamiento.getDireccion() == null
                || alojamiento.getDireccion().isBlank()) {
            camposFaltantes.add("direccion");
        }

        if (alojamiento.getCiudad() == null
                || alojamiento.getCiudad().isBlank()) {
            camposFaltantes.add("ciudad");
        }

        if (alojamiento.getPrecioNoche() == null
                || alojamiento.getPrecioNoche().signum() <= 0) {
            camposFaltantes.add("precioNoche");
        }

        if (alojamiento.getCapacidad() == null
                || alojamiento.getCapacidad() <= 0) {
            camposFaltantes.add("capacidad");
        }

        return camposFaltantes;
    }

    public boolean cumpleRequisitosMinimos(Alojamiento alojamiento) {
        return obtenerCamposFaltantes(alojamiento).isEmpty();
    }
}
