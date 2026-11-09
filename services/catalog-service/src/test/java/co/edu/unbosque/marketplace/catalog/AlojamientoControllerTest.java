
package co.edu.unbosque.marketplace.catalog;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import co.edu.unbosque.marketplace.catalog.domain.Alojamiento;
import co.edu.unbosque.marketplace.catalog.domain.PeriodoDisponibilidad;
import co.edu.unbosque.marketplace.catalog.repository.AlojamientoRepository;
import co.edu.unbosque.marketplace.catalog.repository.PeriodoDisponibilidadRepository;
import co.edu.unbosque.marketplace.catalog.client.AuditClient;
import co.edu.unbosque.marketplace.catalog.client.BookingResenaClient;
import co.edu.unbosque.marketplace.catalog.dto.ResenaResponse;
import co.edu.unbosque.marketplace.catalog.dto.ResumenResenasResponse;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:catalog_alojamiento_test",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.flyway.enabled=false"
})
@AutoConfigureMockMvc
@Transactional
class AlojamientoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AlojamientoRepository alojamientoRepository;

    @Autowired
    private PeriodoDisponibilidadRepository periodoDisponibilidadRepository;

    // Simula Backoffice para no depender de un servicio externo.
    @MockitoBean
    private AuditClient auditClient;

    @MockitoBean
    private BookingResenaClient bookingResenaClient;

    // PRUEBAS SCRUM-24: CONSULTAR DETALLE DE PUBLICACION

    @Test
    @WithMockUser(username = "200", roles = "GUEST")
    void permiteConsultarDetalleDeAlojamientoActivo() throws Exception {

        Alojamiento alojamiento = new Alojamiento(
                100L,
                "Apartamento activo",
                "Apartamento cerca del centro",
                "Calle 10",
                "Bogota",
                new BigDecimal("150000"),
                4
        );

        alojamiento.actualizarDatosComplementarios(
                "APARTAMENTO",
                "No fumar. No se permiten fiestas.",
                "Cancelación flexible.",
                java.util.List.of("WiFi", "Cocina"),
                java.util.List.of("https://ejemplo.com/imagen1.jpg")
        );

        alojamiento.cambiarEstadoA("ACTIVA");
        alojamiento = alojamientoRepository.save(alojamiento);

        mockMvc.perform(get("/api/alojamientos/" + alojamiento.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(alojamiento.getId()))
                .andExpect(jsonPath("$.titulo").value("Apartamento activo"))
                .andExpect(jsonPath("$.descripcion")
                        .value("Apartamento cerca del centro"))
                .andExpect(jsonPath("$.direccion").value("Calle 10"))
                .andExpect(jsonPath("$.ciudad").value("Bogota"))
                .andExpect(jsonPath("$.precioNoche").value(150000))
                .andExpect(jsonPath("$.capacidad").value(4))
                .andExpect(jsonPath("$.estado").value("ACTIVA"))
                .andExpect(jsonPath("$.tipoAlojamiento").value("APARTAMENTO"))
                .andExpect(jsonPath("$.reglas").value("No fumar. No se permiten fiestas."))
                .andExpect(jsonPath("$.politicaCancelacion").value("Cancelación flexible."))
                .andExpect(jsonPath("$.servicios[0]").value("WiFi"))
                .andExpect(jsonPath("$.servicios[1]").value("Cocina"))
                .andExpect(jsonPath("$.imagenes[0]").value("https://ejemplo.com/imagen1.jpg"));
    }

    @Test
    @WithMockUser(username = "200", roles = "GUEST")
    void devuelve404CuandoElAlojamientoNoEstaActivo() throws Exception {

        Alojamiento alojamiento = alojamientoRepository.save(
                new Alojamiento(
                        100L,
                        "Apartamento en borrador",
                        "Alojamiento no publicado",
                        "Calle 10",
                        "Bogota",
                        new BigDecimal("150000"),
                        4
                )
        );

        mockMvc.perform(get("/api/alojamientos/" + alojamiento.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "200", roles = "GUEST")
    void devuelve404CuandoElAlojamientoNoExiste() throws Exception {

        mockMvc.perform(get("/api/alojamientos/999999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "200", roles = "GUEST")
    void detalleDeAlojamientoActivoIncluyePeriodosDeDisponibilidad() throws Exception {

        Alojamiento alojamiento = new Alojamiento(
                100L,
                "Apartamento con disponibilidad",
                "Apartamento cerca del centro",
                "Calle 10",
                "Bogota",
                new BigDecimal("150000"),
                4
        );

        alojamiento.cambiarEstadoA("ACTIVA");
        alojamiento = alojamientoRepository.save(alojamiento);

        PeriodoDisponibilidad periodo = new PeriodoDisponibilidad(
                alojamiento,
                LocalDate.of(2026, 12, 1),
                LocalDate.of(2026, 12, 10)
        );

        periodoDisponibilidadRepository.save(periodo);

        mockMvc.perform(get("/api/alojamientos/" + alojamiento.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.periodosDisponibilidad").isArray())
                .andExpect(jsonPath("$.periodosDisponibilidad.length()").value(1))
                .andExpect(jsonPath("$.periodosDisponibilidad[0].alojamientoId")
                        .value(alojamiento.getId()))
                .andExpect(jsonPath("$.periodosDisponibilidad[0].fechaInicio")
                        .value("2026-12-01"))
                .andExpect(jsonPath("$.periodosDisponibilidad[0].fechaFin")
                        .value("2026-12-10"))
                .andExpect(jsonPath("$.periodosDisponibilidad[0].estado")
                        .value("DISPONIBLE"));
    }

    @Test
    @WithMockUser(username = "200", roles = "GUEST")
    void detalleDeAlojamientoIncluyePromedioYResenasVisibles() throws Exception {

        Alojamiento alojamiento = new Alojamiento(
                100L,
                "Apartamento con resenas",
                "Alojamiento para pruebas",
                "Calle 10",
                "Bogota",
                new BigDecimal("150000"),
                4
        );

        alojamiento.cambiarEstadoA("ACTIVA");
        alojamiento = alojamientoRepository.save(alojamiento);

        when(bookingResenaClient.consultarResenas(alojamiento.getId()))
                .thenReturn(new ResumenResenasResponse(
                        new BigDecimal("4.50"),
                        2,
                        List.of(
                                new ResenaResponse(
                                        1L,
                                        201L,
                                        5,
                                        "Excelente alojamiento",
                                        LocalDateTime.of(2026, 11, 1, 10, 0)
                                ),
                                new ResenaResponse(
                                        2L,
                                        202L,
                                        4,
                                        "Muy buena experiencia",
                                        LocalDateTime.of(2026, 10, 28, 15, 30)
                                )
                        )
                ));

        mockMvc.perform(get("/api/alojamientos/" + alojamiento.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.promedioCalificacion").value(4.50))
                .andExpect(jsonPath("$.cantidadResenas").value(2))
                .andExpect(jsonPath("$.resenas.length()").value(2))
                .andExpect(jsonPath("$.resenas[0].calificacion").value(5))
                .andExpect(jsonPath("$.resenas[0].comentario")
                        .value("Excelente alojamiento"))
                .andExpect(jsonPath("$.resenas[1].calificacion").value(4))
                .andExpect(jsonPath("$.resenas[1].comentario")
                        .value("Muy buena experiencia"));
    }
    // PRUEBAS ORIGINALES

    @Test
    @WithMockUser(username = "100", roles = "HOST")
    void permiteRegistrarAlojamientoComoAnfitrion() throws Exception {
        mockMvc.perform(post("/api/alojamientos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "hostId": 200,
                                    "titulo": "Apartamento de prueba",
                                    "descripcion": "Alojamiento para pruebas",
                                    "direccion": "Calle 10",
                                    "ciudad": "Bogota",
                                    "precioNoche": 150000,
                                    "capacidad": 4
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.hostId").value(100))
                .andExpect(jsonPath("$.titulo").value("Apartamento de prueba"))
                .andExpect(jsonPath("$.estado").value("BORRADOR"));
    }

    @Test
    void rechazaRegistroSinAutenticacion() throws Exception {
        mockMvc.perform(post("/api/alojamientos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "titulo": "Apartamento de prueba",
                                    "precioNoche": 150000,
                                    "capacidad": 4
                                }
                                """))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "200", roles = "GUEST")
    void rechazaRegistroCuandoElUsuarioNoEsAnfitrion() throws Exception {
        mockMvc.perform(post("/api/alojamientos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "titulo": "Apartamento de prueba",
                                    "precioNoche": 150000,
                                    "capacidad": 4
                                }
                                """))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "100", roles = "HOST")
    void rechazaDatosObligatoriosInvalidos() throws Exception {
        mockMvc.perform(post("/api/alojamientos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "titulo": "",
                                    "precioNoche": 0,
                                    "capacidad": 0
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    // PRUEBAS DE PRECIO

    @Test
    @WithMockUser(username = "100", roles = "HOST")
    void rechazaPrecioEnCero() throws Exception {
        mockMvc.perform(post("/api/alojamientos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "titulo": "Apartamento de prueba",
                                    "precioNoche": 0,
                                    "capacidad": 4
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "100", roles = "HOST")
    void rechazaPrecioNegativo() throws Exception {
        mockMvc.perform(post("/api/alojamientos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "titulo": "Apartamento de prueba",
                                    "precioNoche": -100,
                                    "capacidad": 4
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "100", roles = "HOST")
    void rechazaPrecioNulo() throws Exception {
        mockMvc.perform(post("/api/alojamientos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "titulo": "Apartamento de prueba",
                                    "precioNoche": null,
                                    "capacidad": 4
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    // PRUEBAS DE CAPACIDAD

    @Test
    @WithMockUser(username = "100", roles = "HOST")
    void rechazaCapacidadEnCero() throws Exception {
        mockMvc.perform(post("/api/alojamientos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "titulo": "Apartamento de prueba",
                                    "precioNoche": 150000,
                                    "capacidad": 0
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "100", roles = "HOST")
    void rechazaCapacidadNegativa() throws Exception {
        mockMvc.perform(post("/api/alojamientos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "titulo": "Apartamento de prueba",
                                    "precioNoche": 150000,
                                    "capacidad": -1
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "100", roles = "HOST")
    void rechazaCapacidadNula() throws Exception {
        mockMvc.perform(post("/api/alojamientos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "titulo": "Apartamento de prueba",
                                    "precioNoche": 150000,
                                    "capacidad": null
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "200", roles = "ADMIN")
    void administradorPuedeActivarAlojamientoDeOtroUsuario() throws Exception {

        Alojamiento alojamiento = alojamientoRepository.save(
                new Alojamiento(
                        100L,
                        "Apartamento administrador",
                        "Alojamiento para pruebas",
                        "Calle 10",
                        "Bogota",
                        new BigDecimal("150000"),
                        4
                )
        );

        mockMvc.perform(post("/api/alojamientos/"
                        + alojamiento.getId() + "/activar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("ACTIVA"));
    }

    @Test
    @WithMockUser(username = "200", roles = "HOST")
    void anfitrionNoPuedeActivarAlojamientoDeOtroUsuario() throws Exception {

        Alojamiento alojamiento = alojamientoRepository.save(
                new Alojamiento(
                        100L,
                        "Apartamento de otro usuario",
                        "Alojamiento para pruebas",
                        "Calle 10",
                        "Bogota",
                        new BigDecimal("150000"),
                        4
                )
        );

        mockMvc.perform(post("/api/alojamientos/"
                        + alojamiento.getId() + "/activar"))
                .andExpect(status().isForbidden());
    }

    // PRUEBA DE VALORES MINIMOS VALIDOS

    @Test
    @WithMockUser(username = "100", roles = "HOST")
    void permitePrecioYCapacidadEnValoresMinimos() throws Exception {
        mockMvc.perform(post("/api/alojamientos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "titulo": "Apartamento de prueba",
                                    "precioNoche": 0.01,
                                    "capacidad": 1
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.hostId").value(100));
    }

    // PRUEBAS DE ACTIVACION - SCRUM-70

    @Test
    @WithMockUser(username = "100", roles = "HOST")
    void activaAlojamientoCuandoCumpleTodosLosRequisitos() throws Exception {

        Alojamiento alojamiento = alojamientoRepository.save(
                new Alojamiento(
                        100L,
                        "Apartamento completo",
                        "Alojamiento para pruebas",
                        "Calle 10",
                        "Bogota",
                        new BigDecimal("150000"),
                        4
                )
        );

        mockMvc.perform(post("/api/alojamientos/"
                        + alojamiento.getId() + "/activar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("ACTIVA"));
    }

    @Test
    @WithMockUser(username = "100", roles = "HOST")
    void rechazaActivacionCuandoFaltanDatosYConservaEstado() throws Exception {

        Alojamiento alojamiento = alojamientoRepository.save(
                new Alojamiento(
                        100L,
                        "Apartamento incompleto",
                        null,
                        null,
                        "Bogota",
                        new BigDecimal("150000"),
                        4
                )
        );

        mockMvc.perform(post("/api/alojamientos/"
                        + alojamiento.getId() + "/activar"))
                .andExpect(status().isBadRequest())
                .andExpect(status().reason(
                        containsString("Campos faltantes")
                ));

        Alojamiento alojamientoGuardado = alojamientoRepository
                .findById(alojamiento.getId())
                .orElseThrow();

        assertEquals("BORRADOR", alojamientoGuardado.getEstado());
    }

    // NUEVAS PRUEBAS - SCRUM-22: EDITAR Y PAUSAR PUBLICACION

    @Test
    @WithMockUser(username = "100", roles = "HOST")
    void propietarioPuedeEditarSuAlojamientoYRegistrarAuditoria() throws Exception {

        Alojamiento alojamiento = alojamientoRepository.save(
                new Alojamiento(
                        100L,
                        "Apartamento original",
                        "Descripcion original",
                        "Calle 10",
                        "Bogota",
                        new BigDecimal("150000"),
                        4
                )
        );

        mockMvc.perform(put("/api/alojamientos/" + alojamiento.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "titulo": "Apartamento actualizado",
                                    "descripcion": "Nueva descripcion",
                                    "direccion": "Carrera 15",
                                    "ciudad": "Medellin",
                                    "precioNoche": 200000,
                                    "capacidad": 5
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titulo").value("Apartamento actualizado"))
                .andExpect(jsonPath("$.precioNoche").value(200000))
                .andExpect(jsonPath("$.capacidad").value(5));

        Alojamiento actualizado = alojamientoRepository
                .findById(alojamiento.getId())
                .orElseThrow();

        assertEquals("Apartamento actualizado", actualizado.getTitulo());
        assertEquals("Nueva descripcion", actualizado.getDescripcion());
        assertEquals("Carrera 15", actualizado.getDireccion());
        assertEquals("Medellin", actualizado.getCiudad());
        assertEquals(new BigDecimal("200000"), actualizado.getPrecioNoche());
        assertEquals(5, actualizado.getCapacidad());

        verify(auditClient).registrarEdicion(100L, alojamiento.getId());
    }

    @Test
    @WithMockUser(username = "200", roles = "HOST")
    void otroAnfitrionNoPuedeEditarAlojamientoAjeno() throws Exception {

        Alojamiento alojamiento = alojamientoRepository.save(
                new Alojamiento(
                        100L,
                        "Apartamento original",
                        "Descripcion original",
                        "Calle 10",
                        "Bogota",
                        new BigDecimal("150000"),
                        4
                )
        );

        mockMvc.perform(put("/api/alojamientos/" + alojamiento.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "titulo": "Intento de modificacion",
                                    "descripcion": "Cambio no autorizado",
                                    "direccion": "Calle 20",
                                    "ciudad": "Cali",
                                    "precioNoche": 200000,
                                    "capacidad": 5
                                }
                                """))
                .andExpect(status().isForbidden());

        Alojamiento sinCambios = alojamientoRepository
                .findById(alojamiento.getId())
                .orElseThrow();

        assertEquals("Apartamento original", sinCambios.getTitulo());
        assertEquals(new BigDecimal("150000"), sinCambios.getPrecioNoche());
    }

    @Test
    @WithMockUser(username = "200", roles = "ADMIN")
    void administradorPuedeEditarAlojamientoDeOtroUsuario() throws Exception {

        Alojamiento alojamiento = alojamientoRepository.save(
                new Alojamiento(
                        100L,
                        "Apartamento original",
                        "Descripcion original",
                        "Calle 10",
                        "Bogota",
                        new BigDecimal("150000"),
                        4
                )
        );

        mockMvc.perform(put("/api/alojamientos/" + alojamiento.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "titulo": "Apartamento editado por admin",
                                    "descripcion": "Descripcion modificada",
                                    "direccion": "Calle 30",
                                    "ciudad": "Bogota",
                                    "precioNoche": 250000,
                                    "capacidad": 6
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titulo").value("Apartamento editado por admin"));

        verify(auditClient).registrarEdicion(200L, alojamiento.getId());
    }

    @Test
    @WithMockUser(username = "100", roles = "HOST")
    void propietarioPuedePausarAlojamientoActivoYRegistrarAuditoria() throws Exception {

        Alojamiento alojamiento = new Alojamiento(
                100L,
                "Apartamento activo",
                "Alojamiento para pruebas",
                "Calle 10",
                "Bogota",
                new BigDecimal("150000"),
                4
        );

        alojamiento.cambiarEstadoA("ACTIVA");
        alojamiento = alojamientoRepository.save(alojamiento);

        mockMvc.perform(patch("/api/alojamientos/"
                        + alojamiento.getId() + "/pausar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("PAUSADA"));

        verify(auditClient).registrarPausa(100L, alojamiento.getId());
    }

    @Test
    @WithMockUser(username = "100", roles = "HOST")
    void rechazaPausarAlojamientoQueNoEstaActivo() throws Exception {

        Alojamiento alojamiento = alojamientoRepository.save(
                new Alojamiento(
                        100L,
                        "Apartamento en borrador",
                        "Alojamiento para pruebas",
                        "Calle 10",
                        "Bogota",
                        new BigDecimal("150000"),
                        4
                )
        );

        mockMvc.perform(patch("/api/alojamientos/"
                        + alojamiento.getId() + "/pausar"))
                .andExpect(status().isConflict());

        Alojamiento sinCambios = alojamientoRepository
                .findById(alojamiento.getId())
                .orElseThrow();

        assertEquals("BORRADOR", sinCambios.getEstado());
    }

    // SCRUM-22: VALIDACION DE PROPIEDAD AL PAUSAR

    @Test
    @WithMockUser(username = "200", roles = "HOST")
    void otroAnfitrionNoPuedePausarAlojamientoAjeno() throws Exception {

        Alojamiento alojamiento = new Alojamiento(
                100L,
                "Apartamento de otro anfitrion",
                "Alojamiento para pruebas",
                "Calle 10",
                "Bogota",
                new BigDecimal("150000"),
                4
        );

        alojamiento.cambiarEstadoA("ACTIVA");
        alojamiento = alojamientoRepository.save(alojamiento);

        mockMvc.perform(patch("/api/alojamientos/"
                        + alojamiento.getId() + "/pausar"))
                .andExpect(status().isForbidden());

        Alojamiento sinCambios = alojamientoRepository
                .findById(alojamiento.getId())
                .orElseThrow();

        assertEquals("ACTIVA", sinCambios.getEstado());
    }

    // SCRUM-72: BUSQUEDA CON FILTROS COMBINADOS

    @Test
    @WithMockUser(username = "200", roles = "GUEST")
    void buscaAlojamientosAplicandoTodosLosFiltrosYExcluyeBorradores() throws Exception {

        Alojamiento alojamientoCoincidente = new Alojamiento(
                100L,
                "Apartamento con todos los filtros",
                "Alojamiento para pruebas",
                "Calle 10",
                "Bogota",
                new BigDecimal("180000"),
                5
        );

        alojamientoCoincidente.actualizarDatosComplementarios(
                "APARTAMENTO",
                "No fumar",
                "Flexible",
                List.of("WiFi", "Cocina"),
                List.of()
        );
        alojamientoCoincidente.cambiarEstadoA("ACTIVA");
        alojamientoRepository.save(alojamientoCoincidente);

        Alojamiento alojamientoSinTodosLosServicios = new Alojamiento(
                101L,
                "Apartamento con servicios incompletos",
                "Alojamiento para pruebas",
                "Calle 20",
                "Bogota",
                new BigDecimal("180000"),
                5
        );

        alojamientoSinTodosLosServicios.actualizarDatosComplementarios(
                "APARTAMENTO",
                "No fumar",
                "Flexible",
                List.of("WiFi", "Piscina"),
                List.of()
        );
        alojamientoSinTodosLosServicios.cambiarEstadoA("ACTIVA");
        alojamientoRepository.save(alojamientoSinTodosLosServicios);

        Alojamiento alojamientoEnBorrador = new Alojamiento(
                102L,
                "Apartamento en borrador",
                "Alojamiento para pruebas",
                "Calle 30",
                "Bogota",
                new BigDecimal("180000"),
                5
        );

        alojamientoEnBorrador.actualizarDatosComplementarios(
                "APARTAMENTO",
                "No fumar",
                "Flexible",
                List.of("WiFi", "Cocina"),
                List.of()
        );
        alojamientoRepository.save(alojamientoEnBorrador);

        mockMvc.perform(get("/api/alojamientos/buscar")
                        .param("ciudad", "Bogota")
                        .param("capacidad", "4")
                        .param("precioMinimo", "100000")
                        .param("precioMaximo", "250000")
                        .param("tipoAlojamiento", "APARTAMENTO")
                        .param("servicios", "WiFi")
                        .param("servicios", "Cocina"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].titulo")
                        .value("Apartamento con todos los filtros"));
    }
}
