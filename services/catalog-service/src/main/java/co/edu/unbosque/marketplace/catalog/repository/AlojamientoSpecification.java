package co.edu.unbosque.marketplace.catalog.repository;

import co.edu.unbosque.marketplace.catalog.domain.Alojamiento;
import co.edu.unbosque.marketplace.catalog.domain.PeriodoDisponibilidad;
import co.edu.unbosque.marketplace.catalog.dto.BusquedaAlojamientoRequest;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;

public final class AlojamientoSpecification {

    private AlojamientoSpecification() {
    }

    public static Specification<Alojamiento> conFiltros(
            BusquedaAlojamientoRequest filtros) {

        return (root, query, criteriaBuilder) -> {
            List<Predicate> condiciones = new ArrayList<>();

            condiciones.add(
                    criteriaBuilder.equal(root.get("estado"), "ACTIVA")
            );

            if (filtros.ciudad() != null && !filtros.ciudad().isBlank()) {
                condiciones.add(
                        criteriaBuilder.like(
                                criteriaBuilder.lower(root.get("ciudad")),
                                "%" + filtros.ciudad().trim().toLowerCase() + "%"
                        )
                );
            }

            if (filtros.capacidad() != null) {
                condiciones.add(
                        criteriaBuilder.greaterThanOrEqualTo(
                                root.get("capacidad"),
                                filtros.capacidad()
                        )
                );
            }

            if (filtros.precioMinimo() != null) {
                condiciones.add(
                        criteriaBuilder.greaterThanOrEqualTo(
                                root.get("precioNoche"),
                                filtros.precioMinimo()
                        )
                );
            }

            if (filtros.precioMaximo() != null) {
                condiciones.add(
                        criteriaBuilder.lessThanOrEqualTo(
                                root.get("precioNoche"),
                                filtros.precioMaximo()
                        )
                );
            }

            if (filtros.tipoAlojamiento() != null
                    && !filtros.tipoAlojamiento().isBlank()) {
                condiciones.add(
                        criteriaBuilder.equal(
                                criteriaBuilder.lower(root.get("tipoAlojamiento")),
                                filtros.tipoAlojamiento().trim().toLowerCase()
                        )
                );
            }

            if (filtros.servicios() != null) {
                for (String servicio : filtros.servicios()) {
                    if (servicio == null || servicio.isBlank()) {
                        continue;
                    }

                    Subquery<Long> subconsulta = query.subquery(Long.class);
                    var alojamientoCorrelacionado = subconsulta.correlate(root);
                    Join<Alojamiento, String> servicioJoin =
                            alojamientoCorrelacionado.join("servicios");

                    subconsulta.select(criteriaBuilder.literal(1L))
                            .where(
                                    criteriaBuilder.equal(
                                            criteriaBuilder.lower(servicioJoin),
                                            servicio.trim().toLowerCase()
                                    )
                            );

                    condiciones.add(criteriaBuilder.exists(subconsulta));
                }
            }

            // SCRUM-73: el alojamiento debe estar disponible durante todo el rango.
            if (filtros.fechaInicio() != null && filtros.fechaFin() != null) {
                Subquery<Long> disponibilidad = query.subquery(Long.class);
                Root<PeriodoDisponibilidad> periodo =
                        disponibilidad.from(PeriodoDisponibilidad.class);

                disponibilidad.select(criteriaBuilder.literal(1L))
                        .where(
                                criteriaBuilder.equal(
                                        periodo.get("alojamiento"),
                                        root
                                ),
                                criteriaBuilder.equal(
                                        periodo.get("estado"),
                                        PeriodoDisponibilidad.Estado.DISPONIBLE
                                ),
                                criteriaBuilder.lessThanOrEqualTo(
                                        periodo.get("fechaInicio"),
                                        filtros.fechaInicio()
                                ),
                                criteriaBuilder.greaterThanOrEqualTo(
                                        periodo.get("fechaFin"),
                                        filtros.fechaFin()
                                )
                        );

                condiciones.add(criteriaBuilder.exists(disponibilidad));
            }

            return criteriaBuilder.and(
                    condiciones.toArray(new Predicate[0])
            );
        };
    }
}