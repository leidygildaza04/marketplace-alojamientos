package co.edu.unbosque.marketplace.catalog.dto;

import java.math.BigDecimal;
import java.util.List;

public record ResumenResenasResponse(
        BigDecimal promedio,
        long cantidad,
        List<ResenaResponse> resenas
) {
}