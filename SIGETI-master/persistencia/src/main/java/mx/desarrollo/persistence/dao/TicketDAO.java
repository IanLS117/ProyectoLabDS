package mx.desarrollo.persistence.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.LockModeType;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.UUID;
import mx.desarrollo.entity.Equipo;
import mx.desarrollo.entity.Ticket;
import mx.desarrollo.entity.TipoIncidencia;
import mx.desarrollo.entity.Usuario;
import mx.desarrollo.entity.UsuarioFinal;
import mx.desarrollo.persistence.persistence.HibernateUtil;
import jakarta.persistence.TypedQuery;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import mx.desarrollo.entity.Rol;

public class TicketDAO {

    private final Supplier<EntityManager> entityManagers;

    public TicketDAO() {
        this(HibernateUtil::getEntityManager);
    }

    TicketDAO(Supplier<EntityManager> entityManagers) {
        this.entityManagers = entityManagers;
    }

    public Ticket registrar(String rfc, String numeroInventario, Integer idTipo, String descripcion,
            Function<Map<Usuario, Long>, Usuario> seleccionarTecnico) {
        EntityManager manager = entityManagers.get();
        EntityTransaction transaccion = manager.getTransaction();
        try {
            transaccion.begin();

            if (manager.find(Rol.class, 2, LockModeType.PESSIMISTIC_WRITE) == null) {
                throw new IllegalArgumentException("No hay tecnicos disponibles para asignar el ticket.");
            }
            Usuario actor = manager.find(Usuario.class, rfc);
            if (actor == null || !actor.tienePermiso("crear_ticket")) {
                throw new IllegalArgumentException("No tienes permiso para registrar tickets.");
            }
            UsuarioFinal solicitante = manager.find(UsuarioFinal.class, rfc);
            if (solicitante == null) {
                throw new IllegalArgumentException("Tu cuenta no tiene un perfil de usuario final.");
            }
            Equipo equipo = manager.find(Equipo.class, numeroInventario, LockModeType.PESSIMISTIC_WRITE);
            if (equipo == null || equipo.getUsuarioAsignado() == null
                    || !rfc.equals(equipo.getUsuarioAsignado().getRfcUsuario())) {
                throw new IllegalArgumentException("Selecciona un equipo asignado a tu cuenta.");
            }
            TipoIncidencia tipo = manager.find(TipoIncidencia.class, idTipo, LockModeType.PESSIMISTIC_READ);
            if (tipo == null || !tipo.isActivo()) {
                throw new IllegalArgumentException("Selecciona un tipo de incidencia válido.");
            }
            Ticket ticket = new Ticket();
            ticket.setFolio("TCK-" + UUID.randomUUID().toString().toUpperCase(Locale.ROOT));
            ticket.setUsuario(solicitante);

            Map<Usuario, Long> cargas = contarTicketsActivosPorTecnico(manager);
            Usuario tecnico = seleccionarTecnico.apply(cargas);
            if (tecnico == null || !cargas.containsKey(tecnico)) {
                throw new IllegalArgumentException("No hay tecnicos disponibles para asignar el ticket.");
            }
            ticket.setTecnico(tecnico);
            ticket.setEquipo(equipo);
            ticket.setTipoIncidencia(tipo);
            ticket.setDescripcion(descripcion);
            ticket.setEstado("Enviado");
            ticket.setFechaCreacion(LocalDateTime.now());
            manager.persist(ticket);
            transaccion.commit();
            return ticket;
        } catch (RuntimeException error) {
            if (transaccion.isActive()) {
                transaccion.rollback();
            }
            throw error;
        } finally {
            manager.close();
        }
    }

    public Map<Usuario, Long> contarTicketsActivosPorTecnico(EntityManager manager) {
        List<Usuario> tecnicos = manager.createQuery(
                "SELECT u FROM Usuario u WHERE u.rol.idRol = 2 ORDER BY u.rfc", Usuario.class)
                .getResultList();
        Map<String, Long> totales = new LinkedHashMap<>();
        List<Object[]> resultados = manager.createQuery("SELECT t.tecnico.rfc, COUNT(t) FROM Ticket t "
                + "WHERE t.estado IN (:estados) AND t.tecnico IS NOT NULL "
                + "GROUP BY t.tecnico.rfc", Object[].class)
                .setParameter("estados", List.of("Enviado", "En atención"))
                .getResultList();
        for (Object[] fila : resultados) {
            String rfcTecnico = (String) fila[0];
            Long cantidadTickets = (Long) fila[1];
            totales.put(rfcTecnico, cantidadTickets);
        }
        Map<Usuario, Long> cargas = new LinkedHashMap<>();
        for (Usuario tecnico : tecnicos) {
            cargas.put(tecnico, totales.getOrDefault(tecnico.getRfc(), 0L));
        }
        return cargas;
    }

    public List<Ticket> obtenerTodos() {
        EntityManager em = entityManagers.get();
        try {
            TypedQuery<Ticket> query = em.createQuery(
                    "SELECT t FROM Ticket t JOIN FETCH t.equipo ORDER BY t.fechaCreacion DESC",
                    Ticket.class
            );
            return query.getResultList();
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    public List<Ticket> obtenerTodosAdmin(String rfc) {
        EntityManager manager = entityManagers.get();
        try {
            Usuario actor = manager.find(Usuario.class, rfc);
            if (actor == null || !actor.tienePermiso("administrar_tickets")) {
                throw new IllegalArgumentException("No tienes permiso para consultar todos los tickets.");
            }
            TypedQuery<Ticket> consulta = manager.createQuery(
                    "SELECT t FROM Ticket t "
                            + "JOIN FETCH t.usuario "
                            + "JOIN FETCH t.equipo "
                            + "JOIN FETCH t.tipoIncidencia "
                            + "ORDER BY t.fechaCreacion DESC",
                    Ticket.class
            );
            return consulta.getResultList();
        } finally {
            manager.close();
        }
    }

    public List<Ticket> obtenerPorUsuario(String rfc) {
        if (rfc == null || rfc.isBlank()) {
            throw new IllegalArgumentException("Inicia sesión para consultar tus tickets.");
        }
        EntityManager manager = entityManagers.get();
        try {
            Usuario actor = manager.find(Usuario.class, rfc);
            if (actor == null || !actor.tienePermiso("crear_ticket")) {
                throw new IllegalArgumentException("No tienes permiso para consultar tickets de usuario final.");
            }
            if (manager.find(UsuarioFinal.class, rfc) == null) {
                throw new IllegalArgumentException("Tu cuenta no tiene un perfil de usuario final.");
            }
            return manager.createQuery(
                    "SELECT t FROM Ticket t "
                            + "JOIN FETCH t.equipo "
                            + "JOIN FETCH t.tipoIncidencia "
                            + "WHERE t.usuario.rfcUsuario = :rfc "
                            + "ORDER BY t.fechaCreacion DESC, t.idTicket DESC", Ticket.class)
                    .setParameter("rfc", rfc)
                    .getResultList();
        } finally {
            manager.close();
        }
    }
}
