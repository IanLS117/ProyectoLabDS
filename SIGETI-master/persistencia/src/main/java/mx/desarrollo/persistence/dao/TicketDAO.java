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

public class TicketDAO {

    public Ticket registrar(String rfc, String numeroInventario, Integer idTipo, String descripcion) {
        EntityManager manager = HibernateUtil.getEntityManager();
        EntityTransaction transaccion = manager.getTransaction();
        try {
            transaccion.begin();
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

    public List<Ticket> obtenerTodos() {
        EntityManager em = HibernateUtil.getEntityManager();
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
}
