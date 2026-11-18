package mx.desarrollo.persistence.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.util.List;
import java.util.Map;
import mx.desarrollo.entity.Ticket;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import static org.junit.jupiter.api.Assertions.*;

class MisTicketsTest {

    private static final String A = "DEMO000001AAA";
    private static final String B = "DEMO000002BBB";
    private static final String SIN_TICKETS = "DEMO000003CCC";
    private static EntityManagerFactory factory;
    private TicketDAO dao;

    @BeforeAll
    static void crearBaseAislada() {
        factory = Persistence.createEntityManagerFactory("persistencePU", Map.of(
                "jakarta.persistence.jdbc.url", "jdbc:h2:mem:mis_tickets;MODE=MySQL;DB_CLOSE_DELAY=-1",
                "jakarta.persistence.jdbc.driver", "org.h2.Driver",
                "jakarta.persistence.jdbc.user", "sa",
                "jakarta.persistence.jdbc.password", "",
                "hibernate.dialect", "org.hibernate.dialect.H2Dialect",
                "hibernate.hbm2ddl.auto", "create-drop"));
    }

    @AfterAll
    static void cerrarBaseAislada() {
        if (factory != null) {
            factory.close();
        }
    }

    @BeforeEach
    void prepararDatos() {
        dao = new TicketDAO(factory::createEntityManager);
        ejecutar(
                "DELETE FROM SEGUIMIENTO", "DELETE FROM ticket", "DELETE FROM equipo",
                "DELETE FROM usuario_final", "DELETE FROM rol_permiso", "DELETE FROM usuario",
                "DELETE FROM rol", "DELETE FROM permiso", "DELETE FROM tipo_incidencia",
                "INSERT INTO permiso (id_permiso, nombre_permiso) VALUES "
                        + "(1, 'crear_ticket'), (2, 'atender_tickets'), (3, 'administrar_tickets')",
                "INSERT INTO rol (id_rol, nombre_rol) VALUES "
                        + "(1, 'Usuario final'), (2, 'Soporte'), (3, 'Administrador')",
                "INSERT INTO rol_permiso (id_rol, id_permiso) VALUES (1,1), (2,2), (3,3)",
                "INSERT INTO usuario (rfc, nombre, apellidos, correo, contrasena, id_rol) VALUES "
                        + "('DEMO000001AAA', 'A', 'Prueba', 'a@example.invalid', 'no-se-usa', 1),"
                        + "('DEMO000002BBB', 'B', 'Prueba', 'b@example.invalid', 'no-se-usa', 1),"
                        + "('DEMO000003CCC', 'C', 'Prueba', 'c@example.invalid', 'no-se-usa', 1),"
                        + "('DEMO000004SSS', 'S', 'Prueba', 's@example.invalid', 'no-se-usa', 2),"
                        + "('DEMO000005DDD', 'D', 'Prueba', 'd@example.invalid', 'no-se-usa', 3),"
                        + "('DEMO000006XXX', 'X', 'Sin perfil', 'x@example.invalid', 'no-se-usa', 1)",
                "INSERT INTO usuario_final (rfc_usuario) VALUES "
                        + "('DEMO000001AAA'), ('DEMO000002BBB'), ('DEMO000003CCC')",
                "INSERT INTO equipo (numero_inventario, marca, modelo, area_asignada, estado_hardware, rfc_usuario_asignado) VALUES "
                        + "('EQ-A', 'Dell', 'OptiPlex', 'A', 'Operativo', 'DEMO000001AAA'),"
                        + "('EQ-B', 'Lenovo', 'ThinkCentre', 'B', 'Operativo', 'DEMO000002BBB')",
                "INSERT INTO tipo_incidencia (id_tipo_incidencia, nombre, activo) VALUES (1, 'Hardware', TRUE)",
                "INSERT INTO ticket (id_ticket, folio, rfc_usuario_final, numero_inventario, id_tipo_incidencia, "
                        + "descripcion_problema, estatus, fecha_hora_creacion) VALUES "
                        + "(1, 'TCK-A-1', 'DEMO000001AAA', 'EQ-A', 1, 'Antiguo sin tecnico', 'Enviado', '2026-11-01 10:00:00'),"
                        + "(2, 'TCK-B-1', 'DEMO000002BBB', 'EQ-B', 1, 'Solo de B', 'Enviado', '2026-11-17 10:00:00'),"
                        + "(3, 'TCK-A-2', 'DEMO000001AAA', 'EQ-A', 1, 'En revision', 'En atención', '2026-11-16 10:00:00'),"
                        + "(4, 'TCK-A-3', 'DEMO000001AAA', 'EQ-A', 1, 'Resuelto', 'Terminado', '2026-11-16 10:00:00')");
    }

    @Test
    void consultaSoloTicketsPropiosOrdenadosConTodosLosEstados() {
        List<Ticket> tickets = dao.obtenerPorUsuario(A);
        assertEquals(List.of("TCK-A-3", "TCK-A-2", "TCK-A-1"),
                tickets.stream().map(Ticket::getFolio).toList());
        assertEquals(List.of("Terminado", "En atención", "Enviado"),
                tickets.stream().map(Ticket::getEstado).toList());
    }

    @Test
    void otroUsuarioNoVeLosTicketsDeA() {
        assertEquals(List.of("TCK-B-1"), dao.obtenerPorUsuario(B).stream().map(Ticket::getFolio).toList());
    }

    @Test
    void usuarioSinTicketsObtieneListaVacia() {
        assertTrue(dao.obtenerPorUsuario(SIN_TICKETS).isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {"DEMO000004SSS", "DEMO000005DDD", "NOEXISTE00000", "' OR '1'='1"})
    void rechazaSoporteAdministradorYActoresInvalidos(String rfc) {
        assertThrows(IllegalArgumentException.class, () -> dao.obtenerPorUsuario(rfc));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" "})
    void rechazaConsultaSinIdentidad(String rfc) {
        assertThrows(IllegalArgumentException.class, () -> dao.obtenerPorUsuario(rfc));
    }

    @Test
    void permisoSinPerfilDeUsuarioFinalNoAutorizaConsulta() {
        assertThrows(IllegalArgumentException.class, () -> dao.obtenerPorUsuario("DEMO000006XXX"));
    }

    @Test
    void consultaRevalidaElPermisoEnBaseDeDatos() {
        ejecutar("DELETE FROM rol_permiso WHERE id_rol = 1");
        assertThrows(IllegalArgumentException.class, () -> dao.obtenerPorUsuario(A));
    }

    @Test
    void equipoYTipoPuedenLeerseDespuesDeCerrarLaConexion() {
        Ticket ticket = dao.obtenerPorUsuario(A).get(0);
        assertEquals("EQ-A", ticket.getEquipo().getNumeroInventario());
        assertEquals("OptiPlex", ticket.getEquipo().getModelo());
        assertEquals("Hardware", ticket.getTipoIncidencia().getNombre());
    }

    @Test
    void nuevaConsultaMuestraUnCambioDeEstadoPersistido() {
        assertEquals("Enviado", dao.obtenerPorUsuario(B).get(0).getEstado());
        ejecutar("UPDATE ticket SET estatus = 'En atención' WHERE id_ticket = 2");
        assertEquals("En atención", dao.obtenerPorUsuario(B).get(0).getEstado());
        assertTrue(dao.obtenerPorUsuario(A).stream().noneMatch(t -> t.getIdTicket() == 2));
    }

    private void ejecutar(String... sentencias) {
        EntityManager manager = factory.createEntityManager();
        try {
            manager.getTransaction().begin();
            for (String sentencia : sentencias) {
                manager.createNativeQuery(sentencia).executeUpdate();
            }
            manager.getTransaction().commit();
        } catch (RuntimeException error) {
            if (manager.getTransaction().isActive()) {
                manager.getTransaction().rollback();
            }
            throw error;
        } finally {
            manager.close();
        }
    }
}
