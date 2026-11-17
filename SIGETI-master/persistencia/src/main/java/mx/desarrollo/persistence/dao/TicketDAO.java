package mx.desarrollo.persistence.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.LockModeType;
import java.time.LocalDateTime;
import java.util.*;

import mx.desarrollo.entity.Equipo;
import mx.desarrollo.entity.Ticket;
import mx.desarrollo.entity.TipoIncidencia;
import mx.desarrollo.entity.Usuario;
import mx.desarrollo.entity.UsuarioFinal;
import mx.desarrollo.persistence.persistence.HibernateUtil;
import jakarta.persistence.TypedQuery;

import static mx.desarrollo.persistence.persistence.HibernateUtil.getEntityManager;

public class TicketDAO {

    public Ticket registrar(String rfc, String numeroInventario, Integer idTipo, String descripcion) {
        EntityManager manager = getEntityManager();
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

    public List<Ticket> obtenerTodosAdmin(String rfc) {
        EntityManager manager = getEntityManager();
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

    public Map<String, Long> contarCargaActivaPorSoporte() {
        EntityManager em = getEntityManager();
        Map<String, Long> carga = new HashMap<>();
        try {
            List<Object[]> resultados = em.createQuery(
                    "SELECT t.soporteAsignado.rfc, COUNT(t) " +
                            "FROM Ticket t " +
                            "WHERE t.soporteAsignado IS NOT NULL " +
                            "AND t.estado <> 'Terminado' " +
                            "GROUP BY t.soporteAsignado.rfc",
                    Object[].class
            ).getResultList();

            for (Object[] fila : resultados) {
                String rfcSoporte = (String) fila[0];
                Long total = (Long) fila[1];
                carga.put(rfcSoporte, total);
            }
            return carga;
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    public Ticket reasignarSoporte(String rfcAdmin, Integer idTicket, String rfcNuevoSoporte) {
        EntityManager em = getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();

            Usuario actor = em.find(Usuario.class, rfcAdmin);
            if (actor == null || !actor.tienePermiso("administrar_tickets")) {
                throw new IllegalArgumentException("No tienes permiso para reasignar tickets.");
            }

            Ticket ticket = em.find(Ticket.class, idTicket);
            if (ticket == null) {
                throw new IllegalArgumentException("No se encontró el ticket con ID: " + idTicket);
            }

            if ("Terminado".equalsIgnoreCase(ticket.getEstado())) {
                throw new IllegalStateException("No se puede reasignar un ticket que ya está terminado.");
            }

            if (ticket.getSoporteAsignado() != null
                    && rfcNuevoSoporte.equals(ticket.getSoporteAsignado().getRfc())) {
                throw new IllegalStateException("El ticket ya se encuentra asignado a este técnico de soporte.");
            }

            Usuario soporteManaged = em.find(Usuario.class, rfcNuevoSoporte);
            if (soporteManaged == null) {
                throw new IllegalArgumentException("No se encontró el técnico de soporte seleccionado.");
            }

            if (soporteManaged.getRol() == null || soporteManaged.getRol().getIdRol() != 2) {
                throw new IllegalArgumentException("El usuario seleccionado no pertenece al área de soporte técnico.");
            }

            ticket.setSoporteAsignado(soporteManaged);

            if ("Enviado".equalsIgnoreCase(ticket.getEstado())) {
                ticket.setEstado("En atención");
            }

            Ticket actualizado = em.merge(ticket);
            tx.commit();
            return actualizado;
        } catch (RuntimeException e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            throw e;
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }
}
