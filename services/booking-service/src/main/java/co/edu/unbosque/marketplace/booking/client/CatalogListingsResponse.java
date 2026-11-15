package co.edu.unbosque.marketplace.booking.client;

import java.util.List;

public record CatalogListingsResponse(
        Long userId,
        List<Long> accommodationIds,
        int count
) {
}