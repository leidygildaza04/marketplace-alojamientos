package co.edu.unbosque.marketplace.booking.service;

import co.edu.unbosque.marketplace.booking.domain.Resena;
import co.edu.unbosque.marketplace.booking.dto.ResenaResponse;
import co.edu.unbosque.marketplace.booking.dto.ResumenResenasResponse;
import co.edu.unbosque.marketplace.booking.repository.ResenaRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ResenaService {

    private final ResenaRepository repository;

    public ResenaService(ResenaRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public ResumenResenasResponse consultarPorAlojamiento(Long alojamientoId) {

        List<Resena> resenas =
                repository.findByAlojamientoIdAndVisibleTrueOrderByFechaCreacionDesc(
                        alojamientoId
                );

        if (resenas.isEmpty()) {
            return new ResumenResenasResponse(
                    null,
                    0,
                    List.of()
            );
        }

        double promedioCalculado = resenas.stream()
                .mapToInt(Resena::getCalificacion)
                .average()
                .orElse(0.0);

        BigDecimal promedio = BigDecimal.valueOf(promedioCalculado)
                .setScale(2, RoundingMode.HALF_UP);

        List<ResenaResponse> respuestas = resenas.stream()
                .map(ResenaResponse::desde)
                .toList();

        return new ResumenResenasResponse(
                promedio,
                resenas.size(),
                respuestas
        );
    }
}
