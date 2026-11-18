package mx.desarrollo.persistence.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import mx.desarrollo.entity.Seguimiento;
import mx.desarrollo.entity.Ticket;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import static org.junit.jupiter.api.Assertions.*;

class AtencionTicketTest {

    private static final String SOPORTE = "DEMO000004SSS";
    private static final String OTRO = "DEMO000006TTT";
    private static final String USUARIO = "DEMO000001AAA";
    private static EntityManagerFactory factory;
    private AtencionTicketDAO dao;

    @BeforeAll
    static void crearBaseAislada() {
        factory = Persistence.createEntityManagerFactory("persistencePU", Map.of(
                "jakarta.persistence.jdbc.url", "jdbc:h2:mem:atencion;MODE=MySQL;DB_CLOSE_DELAY=-1;LOCK_TIMEOUT=5000",
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
        dao = new AtencionTicketDAO(factory::createEntityManager);
        ejecutar("DELETE FROM SEGUIMIENTO", "DELETE FROM ticket", "DELETE FROM equipo",
                "DELETE FROM usuario_final", "DELETE FROM rol_permiso", "DELETE FROM usuario",
                "DELETE FROM rol", "DELETE FROM permiso", "DELETE FROM tipo_incidencia",
                "INSERT INTO permiso (id_permiso, nombre_permiso) VALUES (1, 'crear_ticket'), (2, 'atender_tickets'), (3, 'administrar_tickets')",
                "INSERT INTO rol (id_rol, nombre_rol) VALUES (1, 'Usuario final'), (2, 'Soporte'), (3, 'Administrador')",
                "INSERT INTO rol_permiso (id_rol, id_permiso) VALUES (1,1), (2,2), (3,3)",
                "INSERT INTO usuario (rfc, nombre, apellidos, correo, contrasena, id_rol) VALUES "
                        + "('DEMO000001AAA', 'A', 'Prueba', 'a@example.invalid', 'no-se-usa', 1),"
                        + "('DEMO000002BBB', 'B', 'Prueba', 'b@example.invalid', 'no-se-usa', 1),"
                        + "('DEMO000004SSS', 'S', 'Prueba', 's@example.invalid', 'no-se-usa', 2),"
                        + "('DEMO000006TTT', 'T', 'Prueba', 't@example.invalid', 'no-se-usa', 2),"
                        + "('DEMO000005DDD', 'D', 'Prueba', 'd@example.invalid', 'no-se-usa', 3)",
                "INSERT INTO usuario_final (rfc_usuario) VALUES ('DEMO000001AAA'), ('DEMO000002BBB')",
                "INSERT INTO equipo (numero_inventario, marca, modelo, area_asignada, estado_hardware, rfc_usuario_asignado) VALUES "
                        + "('EQ-A', 'Dell', 'OptiPlex', 'A', 'Operativo', 'DEMO000001AAA'),"
                        + "('EQ-B', 'Lenovo', 'ThinkCentre', 'B', 'Operativo', 'DEMO000002BBB')",
                "INSERT INTO tipo_incidencia (id_tipo_incidencia, nombre, activo) VALUES (1, 'Hardware', TRUE)",
                "INSERT INTO ticket (id_ticket, folio, rfc_usuario_final, rfc_tecnico, numero_inventario, id_tipo_incidencia, descripcion_problema, estatus, fecha_hora_creacion) VALUES "
                        + "(1, 'TCK-1', 'DEMO000001AAA', 'DEMO000004SSS', 'EQ-A', 1, 'Pendiente', 'Enviado', '2026-11-17 09:00:00'),"
                        + "(2, 'TCK-2', 'DEMO000001AAA', 'DEMO000004SSS', 'EQ-A', 1, 'En revision', 'En atención', '2026-11-17 09:00:00'),"
                        + "(3, 'TCK-3', 'DEMO000001AAA', 'DEMO000004SSS', 'EQ-A', 1, 'Resuelto', 'Terminado', '2026-11-17 09:00:00'),"
                        + "(4, 'TCK-4', 'DEMO000002BBB', 'DEMO000006TTT', 'EQ-B', 1, 'Otro tecnico', 'Enviado', '2026-11-17 09:00:00'),"
                        + "(5, 'TCK-5', 'DEMO000001AAA', NULL, 'EQ-A', 1, 'Sin asignar', 'Enviado', '2026-11-17 09:00:00')",
                "INSERT INTO SEGUIMIENTO (ID_Ticket, Numero_Seguimiento, Fecha, Descripcion, Es_Solucion_Final) VALUES "
                        + "(2, 1, '2026-11-17 09:10:00', 'Diagnostico', FALSE),"
                        + "(2, 2, '2026-11-17 09:20:00', 'Pruebas', FALSE),"
                        + "(3, 1, '2026-11-17 09:30:00', 'Solucion anterior', TRUE)");
    }

    @Test
    void listaSoloLosAsignadosIncluyendoTerminados() {
        assertEquals(List.of(3, 2, 1), dao.listarAsignados(SOPORTE).stream().map(Ticket::getIdTicket).toList());
        assertEquals(List.of(4), dao.listarAsignados(OTRO).stream().map(Ticket::getIdTicket).toList());
    }

    @Test
    void iniciaAtencionYElUsuarioVeElEstadoPersistido() {
        Ticket ticket = dao.cambiarEstado(SOPORTE, 1, "Enviado", "En atención", null);
        assertEquals("En atención", ticket.getEstado());
        assertEquals("OptiPlex", ticket.getEquipo().getModelo());
        assertEquals("Hardware", ticket.getTipoIncidencia().getNombre());
        assertEquals("S", ticket.getTecnico().getNombre());
        assertEquals("En atención", new TicketDAO(factory::createEntityManager).obtenerPorUsuario(USUARIO)
                .stream().filter(t -> t.getIdTicket() == 1).findFirst().orElseThrow().getEstado());
        assertTrue(historial(1).isEmpty());
    }

    @Test
    void terminaConUnaSolucionFinalYConservaLasObservaciones() {
        dao.cambiarEstado(SOPORTE, 2, "En atención", "Terminado", "  Se sustituyó el ventilador.  ");
        assertEquals("Terminado", estado(2));
        List<Seguimiento> registros = historial(2);
        assertEquals(3, registros.size());
        Seguimiento solucion = registros.getLast();
        assertEquals(3, solucion.getId().getNumeroSeguimiento());
        assertTrue(solucion.getEsSolucionFinal());
        assertNotNull(solucion.getFecha());
        assertEquals("Se sustituyó el ventilador.", solucion.getDescripcion());
        assertEquals("Diagnostico", registros.getFirst().getDescripcion());
        assertEquals("Terminado", new TicketDAO(factory::createEntityManager).obtenerPorUsuario(USUARIO)
                .stream().filter(t -> t.getIdTicket() == 2).findFirst().orElseThrow().getEstado());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t\n", "\u2003"})
    void cierreSinSolucionNoModificaEstadoNiHistorial(String solucion) {
        assertThrows(IllegalArgumentException.class,
                () -> dao.cambiarEstado(SOPORTE, 2, "En atención", "Terminado", solucion));
        assertEquals("En atención", estado(2));
        assertEquals(2, historial(2).size());
    }

    @Test
    void rechazaTextosMayoresAlLimiteYAdmiteElLimiteExacto() {
        assertThrows(IllegalArgumentException.class,
                () -> dao.cambiarEstado(SOPORTE, 2, "En atención", "Terminado", "x".repeat(4001)));
        assertThrows(IllegalArgumentException.class, () -> dao.registrarObservacion(SOPORTE, 2, "x".repeat(4001)));
        assertEquals("En atención", estado(2));
        assertEquals(2, historial(2).size());
        dao.cambiarEstado(SOPORTE, 2, "En atención", "Terminado", "x".repeat(4000));
        assertEquals(4000, historial(2).getLast().getDescripcion().length());
    }

    @ParameterizedTest
    @CsvSource({"1,Terminado", "1,Enviado", "2,Enviado", "2,Resuelto", "3,En atención", "3,Terminado"})
    void rechazaSaltosRetrocesosReaperturasYEstadosDesconocidos(int id, String destino) {
        String anterior = estado(id);
        int cantidad = historial(id).size();
        assertThrows(IllegalArgumentException.class,
                () -> dao.cambiarEstado(SOPORTE, id, anterior, destino, "Solución"));
        assertEquals(anterior, estado(id));
        assertEquals(cantidad, historial(id).size());
    }

    @Test
    void rechazaUnFormularioConEstadoDesactualizado() {
        assertThrows(IllegalArgumentException.class,
                () -> dao.cambiarEstado(SOPORTE, 2, "Enviado", "Terminado", "Solución"));
        assertEquals("En atención", estado(2));
        assertEquals(2, historial(2).size());
    }

    @ParameterizedTest
    @ValueSource(ints = {4, 5, 999, 0, -1})
    void noPermiteConsultarNiModificarTicketsAjenosSinAsignarOInvalidos(int id) {
        assertThrows(IllegalArgumentException.class, () -> dao.consultar(SOPORTE, id));
        assertThrows(IllegalArgumentException.class, () -> dao.consultarHistorial(SOPORTE, id));
        assertThrows(IllegalArgumentException.class,
                () -> dao.cambiarEstado(SOPORTE, id, "Enviado", "En atención", null));
        assertThrows(IllegalArgumentException.class, () -> dao.registrarObservacion(SOPORTE, id, "Nota"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"DEMO000001AAA", "DEMO000002BBB", "DEMO000005DDD", "NOEXISTE00000"})
    void losRolesSinPermisoNoPuedenLeerNiEscribirAtencion(String rfc) {
        assertThrows(IllegalArgumentException.class, () -> dao.listarAsignados(rfc));
        assertThrows(IllegalArgumentException.class, () -> dao.consultar(rfc, 1));
        assertThrows(IllegalArgumentException.class, () -> dao.consultarHistorial(rfc, 1));
        assertThrows(IllegalArgumentException.class,
                () -> dao.cambiarEstado(rfc, 1, "Enviado", "En atención", null));
        assertThrows(IllegalArgumentException.class, () -> dao.registrarObservacion(rfc, 2, "Nota"));
        assertEquals("Enviado", estado(1));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" "})
    void requiereIdentidadAutenticada(String rfc) {
        assertThrows(IllegalArgumentException.class, () -> dao.listarAsignados(rfc));
    }

    @Test
    void rechazaUnTicketReasignadoDespuesDeSeleccionarlo() {
        dao.consultar(SOPORTE, 1);
        ejecutar("UPDATE ticket SET rfc_tecnico = 'DEMO000006TTT' WHERE id_ticket = 1");
        assertThrows(IllegalArgumentException.class,
                () -> dao.cambiarEstado(SOPORTE, 1, "Enviado", "En atención", null));
        assertEquals("Enviado", estado(1));
    }

    @Test
    void revalidaLosPermisosAntesDeModificar() {
        ejecutar("DELETE FROM rol_permiso WHERE id_rol = 2");
        assertThrows(IllegalArgumentException.class,
                () -> dao.cambiarEstado(SOPORTE, 1, "Enviado", "En atención", null));
        assertEquals("Enviado", estado(1));
    }

    @Test
    void conservaLaNumeracionYGuardaLaObservacionSinMarcarlaComoSolucion() {
        dao.registrarObservacion(SOPORTE, 2, "  Se revisó el ventilador.  ");
        Seguimiento observacion = historial(2).getLast();
        assertEquals(3, observacion.getId().getNumeroSeguimiento());
        assertFalse(observacion.getEsSolucionFinal());
        assertEquals("Se revisó el ventilador.", observacion.getDescripcion());
        assertEquals("En atención", estado(2));
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 3})
    void noAdmiteObservacionesAntesDeIniciarNiDespuesDeCerrar(int id) {
        assertThrows(IllegalArgumentException.class, () -> dao.registrarObservacion(SOPORTE, id, "Nota"));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" \t\n"})
    void noGuardaObservacionesVacias(String texto) {
        assertThrows(IllegalArgumentException.class, () -> dao.registrarObservacion(SOPORTE, 2, texto));
        assertEquals(2, historial(2).size());
    }

    @Test
    void unaSolucionFinalAnteriorImpideDuplicarElCierre() {
        ejecutar("INSERT INTO SEGUIMIENTO (ID_Ticket, Numero_Seguimiento, Fecha, Descripcion, Es_Solucion_Final) "
                + "VALUES (2, 3, CURRENT_TIMESTAMP, 'Solución existente', TRUE)");
        assertThrows(IllegalArgumentException.class,
                () -> dao.cambiarEstado(SOPORTE, 2, "En atención", "Terminado", "Otra solución"));
        assertEquals("En atención", estado(2));
        assertEquals(3, historial(2).size());
    }

    @Test
    void dosCierresSimultaneosGeneranUnaSolaSolucion() throws Exception {
        List<Boolean> resultados = competir(
                () -> cerrarSiProcede("Solución uno"), () -> cerrarSiProcede("Solución dos"));
        assertEquals(1, resultados.stream().filter(Boolean::booleanValue).count());
        assertEquals("Terminado", estado(2));
        assertEquals(1, historial(2).stream().filter(Seguimiento::getEsSolucionFinal).count());
        assertEquals(3, historial(2).size());
    }

    @Test
    void dosObservacionesSimultaneasRecibenNumerosDistintos() throws Exception {
        assertEquals(List.of(true, true), competir(
                () -> { dao.registrarObservacion(SOPORTE, 2, "Nota uno"); return true; },
                () -> { dao.registrarObservacion(SOPORTE, 2, "Nota dos"); return true; }));
        assertEquals(List.of(1, 2, 3, 4), historial(2).stream().map(s -> s.getId().getNumeroSeguimiento()).toList());
        assertTrue(historial(2).stream().noneMatch(Seguimiento::getEsSolucionFinal));
    }

    @Test
    void unaObservacionNoPuedeQuedarDespuesDeLaSolucionFinal() throws Exception {
        List<Boolean> resultados = competir(() -> cerrarSiProcede("Solución"), () -> {
            try {
                dao.registrarObservacion(SOPORTE, 2, "Nota concurrente");
                return true;
            } catch (IllegalArgumentException esperado) {
                return false;
            }
        });
        assertTrue(resultados.getFirst());
        assertEquals("Terminado", estado(2));
        assertTrue(historial(2).getLast().getEsSolucionFinal());
        assertEquals(1, historial(2).stream().filter(Seguimiento::getEsSolucionFinal).count());
    }

    @Test
    void unFalloAlConfirmarRevierteElEstadoYLaSolucion() {
        AtencionTicketDAO conFallo = new AtencionTicketDAO(() -> {
            EntityManager real = factory.createEntityManager();
            EntityTransaction realTx = real.getTransaction();
            EntityTransaction tx = (EntityTransaction) Proxy.newProxyInstance(
                    EntityTransaction.class.getClassLoader(), new Class<?>[]{EntityTransaction.class},
                    (proxy, metodo, argumentos) -> {
                        if ("commit".equals(metodo.getName())) {
                            real.flush();
                            throw new IllegalStateException("Fallo simulado antes de confirmar");
                        }
                        return invocar(realTx, metodo, argumentos);
                    });
            return (EntityManager) Proxy.newProxyInstance(EntityManager.class.getClassLoader(),
                    new Class<?>[]{EntityManager.class}, (proxy, metodo, argumentos) ->
                            "getTransaction".equals(metodo.getName()) ? tx : invocar(real, metodo, argumentos));
        });
        assertThrows(IllegalStateException.class,
                () -> conFallo.cambiarEstado(SOPORTE, 2, "En atención", "Terminado", "Solución"));
        assertEquals("En atención", estado(2));
        assertEquals(2, historial(2).size());
        assertTrue(historial(2).stream().noneMatch(Seguimiento::getEsSolucionFinal));
    }

    private boolean cerrarSiProcede(String solucion) {
        try {
            dao.cambiarEstado(SOPORTE, 2, "En atención", "Terminado", solucion);
            return true;
        } catch (IllegalArgumentException esperado) {
            return false;
        }
    }

    private List<Boolean> competir(Callable<Boolean> primera, Callable<Boolean> segunda) throws Exception {
        var executor = Executors.newFixedThreadPool(2);
        CountDownLatch listos = new CountDownLatch(2);
        CountDownLatch inicio = new CountDownLatch(1);
        try {
            var uno = executor.submit(() -> { listos.countDown(); inicio.await(); return primera.call(); });
            var dos = executor.submit(() -> { listos.countDown(); inicio.await(); return segunda.call(); });
            assertTrue(listos.await(5, TimeUnit.SECONDS));
            inicio.countDown();
            return List.of(uno.get(10, TimeUnit.SECONDS), dos.get(10, TimeUnit.SECONDS));
        } finally {
            inicio.countDown();
            executor.shutdownNow();
        }
    }

    private String estado(int id) {
        EntityManager manager = factory.createEntityManager();
        try {
            return manager.find(Ticket.class, id).getEstado();
        } finally {
            manager.close();
        }
    }

    private List<Seguimiento> historial(int id) {
        EntityManager manager = factory.createEntityManager();
        try {
            return manager.createQuery("SELECT s FROM Seguimiento s WHERE s.id.idTicket = :id "
                    + "ORDER BY s.id.numeroSeguimiento", Seguimiento.class).setParameter("id", id).getResultList();
        } finally {
            manager.close();
        }
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

    private static Object invocar(Object objeto, Method metodo, Object[] argumentos) throws Throwable {
        try {
            return metodo.invoke(objeto, argumentos);
        } catch (InvocationTargetException error) {
            throw error.getCause();
        }
    }
}
