package mx.desarrollo.persistence.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Supplier;
import mx.desarrollo.entity.BitacoraTicket;
import mx.desarrollo.entity.Ticket;
import mx.desarrollo.entity.Usuario;
import mx.desarrollo.persistence.persistence.HibernateUtil;

public class BitacoraTicketDAO {

    private final Supplier<EntityManager> entityManagers;

    public BitacoraTicketDAO() {
        this(HibernateUtil::getEntityManager);
    }

    BitacoraTicketDAO(Supplier<EntityManager> entityManagers) {
        this.entityManagers = entityManagers;
    }

    public static void registrarEvento(EntityManager em, Ticket ticket, Usuario actor,
                                       String tipoEvento, String valorAnterior, String valorNuevo, String observacion) {
        BitacoraTicket entrada = new BitacoraTicket();
        entrada.setTicket(ticket);
        entrada.setActor(actor);
        entrada.setTipoEvento(tipoEvento);
        entrada.setValorAnterior(valorAnterior);
        entrada.setValorNuevo(valorNuevo);
        entrada.setObservacion(observacion);
        entrada.setFechaHora(LocalDateTime.now());
        em.persist(entrada);
    }

    public List<BitacoraTicket> obtenerBitacoraGeneral(String rfcAdmin) {
        EntityManager em = entityManagers.get();
        try {
            Usuario actor = em.find(Usuario.class, rfcAdmin);
            if (actor == null || !actor.tienePermiso("administrar_tickets")) {
                throw new IllegalArgumentException("No tienes permiso para consultar la bitácora de auditoría.");
            }
            return em.createQuery(
                    "SELECT b FROM BitacoraTicket b "
                            + "JOIN FETCH b.ticket t "
                            + "LEFT JOIN FETCH b.actor "
                            + "ORDER BY b.fechaHora DESC, b.idBitacora DESC",
                    BitacoraTicket.class
            ).getResultList();
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    public List<BitacoraTicket> obtenerBitacoraPorTicket(String rfcAdmin, Integer idTicket) {
        EntityManager em = entityManagers.get();
        try {
            Usuario actor = em.find(Usuario.class, rfcAdmin);
            if (actor == null || !actor.tienePermiso("administrar_tickets")) {
                throw new IllegalArgumentException("No tienes permiso para consultar la bitácora de auditoría.");
            }
            return em.createQuery(
                    "SELECT b FROM BitacoraTicket b "
                            + "JOIN FETCH b.ticket t "
                            + "LEFT JOIN FETCH b.actor "
                            + "WHERE t.idTicket = :idTicket "
                            + "ORDER BY b.fechaHora DESC, b.idBitacora DESC",
                    BitacoraTicket.class
            ).setParameter("idTicket", idTicket).getResultList();
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    public Ticket cambiarPrioridad(String rfcAdmin, Integer idTicket, String nuevaPrioridad) {
        EntityManager em = entityManagers.get();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();

            Usuario actor = em.find(Usuario.class, rfcAdmin);
            if (actor == null || !actor.tienePermiso("administrar_tickets")) {
                throw new IllegalArgumentException("No tienes permiso para cambiar la prioridad.");
            }

            Ticket ticket = em.find(Ticket.class, idTicket);
            if (ticket == null) {
                throw new IllegalArgumentException("No se encontró el ticket seleccionado.");
            }

            if ("Terminado".equalsIgnoreCase(ticket.getEstado())) {
                throw new IllegalStateException("No se puede cambiar la prioridad de un ticket terminado.");
            }

            String prioridadAnterior = ticket.getPrioridad() != null ? ticket.getPrioridad() : "Media";
            if (prioridadAnterior.equalsIgnoreCase(nuevaPrioridad)) {
                throw new IllegalStateException("El ticket ya tiene asignada la prioridad " + nuevaPrioridad + ".");
            }

            ticket.setPrioridad(nuevaPrioridad);
            Ticket actualizado = em.merge(ticket);

            registrarEvento(em, actualizado, actor, "PRIORIDAD",
                    prioridadAnterior, nuevaPrioridad,
                    "Cambio de prioridad realizado por administrador");

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