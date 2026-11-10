package co.edu.unbosque.marketplace.booking.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class PagoSimuladoService {

    public boolean procesarPago(String resultadoPago) {

        if (resultadoPago == null
                || (!"APROBADO".equals(resultadoPago)
                && !"RECHAZADO".equals(resultadoPago))) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El resultado del pago debe ser APROBADO o RECHAZADO"
            );
        }

        return "APROBADO".equals(resultadoPago);
    }
}
