package co.edu.unbosque.marketplace.booking.dto;

import co.edu.unbosque.marketplace.booking.domain.Reserva;
import java.time.LocalDate;
import java.math.BigDecimal;

public record ReservaResponse(
        Long id,
        Long userId,
        Long alojamientoId,
        LocalDate checkIn,
        LocalDate checkOut,
        String estado,
        BigDecimal total) {

    public static ReservaResponse desde(Reserva reserva) {
        return new ReservaResponse(reserva.getId(), reserva.getUserId(),
                reserva.getAlojamientoId(), reserva.getCheckIn(),
                reserva.getCheckOut(), reserva.getEstado(), reserva.getTotal());
    }
}
