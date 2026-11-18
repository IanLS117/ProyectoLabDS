package mx.desarrollo.persistence.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.LockModeType;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;
import mx.desarrollo.entity.Seguimiento;
import mx.desarrollo.entity.SeguimientoId;
import mx.desarrollo.entity.Ticket;
import mx.desarrollo.entity.Usuario;
import mx.desarrollo.persistence.persistence.HibernateUtil;

public class AtencionTicketDAO {

    private final Supplier<EntityManager> entityManagers;

    public AtencionTicketDAO() {
        this(HibernateUtil::getEntityManager);
    }

    AtencionTicketDAO(Supplier<EntityManager> entityManagers) {
        this.entityManagers = entityManagers;
    }

    public List<Ticket> listarAsignados(String rfc) {
        return ejecutar(rfc, manager -> manager.createQuery(
                        "SELECT t FROM Ticket t JOIN FETCH t.equipo "
                                + "JOIN FETCH t.tipoIncidencia JOIN FETCH t.tecnico "
                                + "WHERE t.tecnico.rfc = :rfc "
                                + "ORDER BY t.fechaCreacion DESC, t.idTicket DESC", Ticket.class)
                .setParameter("rfc", rfc).getResultList());
    }

    public Ticket consultar(String rfc, Integer idTicket) {
        return ejecutar(rfc, manager -> {
            ticketAsignado(manager, rfc, idTicket, false);
            return detalle(manager, idTicket);
        });
    }

    public List<Seguimiento> consultarHistorial(String rfc, Integer idTicket) {
        return ejecutar(rfc, manager -> {
            ticketAsignado(manager, rfc, idTicket, false);
            return manager.createQuery(
                            "SELECT s FROM Seguimiento s WHERE s.id.idTicket = :id "
                                    + "ORDER BY s.id.numeroSeguimiento", Seguimiento.class)
                    .setParameter("id", idTicket).getResultList();
        });
    }

    public Ticket cambiarEstado(String rfc, Integer idTicket, String estadoEsperado,
                                String nuevoEstado, String solucion) {
        return ejecutar(rfc, manager -> {
            Ticket ticket = ticketAsignado(manager, rfc, idTicket, true);
            Usuario actor = manager.find(Usuario.class, rfc);

            if (!Objects.equals(ticket.getEstado(), estadoEsperado)) {
                throw new IllegalArgumentException("El estado del ticket cambió. Actualiza el listado y vuelve a seleccionarlo.");
            }
            if ("Enviado".equals(ticket.getEstado()) && "En atención".equals(nuevoEstado)) {
                ticket.setEstado("En atención");
                BitacoraTicketDAO.registrarEvento(manager, ticket, actor, "ESTADO",
                        "Enviado", "En atención", "El técnico inició la atención del ticket");
            } else if ("En atención".equals(ticket.getEstado()) && "Terminado".equals(nuevoEstado)) {
                String texto = validarTexto(solucion, "La solución es obligatoria para terminar el ticket.");
                Long soluciones = manager.createQuery(
                                "SELECT COUNT(s) FROM Seguimiento s WHERE s.id.idTicket = :id "
                                        + "AND s.esSolucionFinal = true", Long.class)
                        .setParameter("id", idTicket).getSingleResult();
                if (soluciones > 0) {
                    throw new IllegalArgumentException("El ticket ya tiene una solución final registrada.");
                }
                registrarSeguimiento(manager, ticket, texto, true);
                ticket.setEstado("Terminado");
                BitacoraTicketDAO.registrarEvento(manager, ticket, actor, "ESTADO",
                        "En atención", "Terminado", "Ticket cerrado con solución final");
            } else {
                throw new IllegalArgumentException("Solo puedes avanzar de Enviado a En atención y de En atención a Terminado.");
            }
            return detalle(manager, idTicket);
        });
    }

    public void registrarObservacion(String rfc, Integer idTicket, String descripcion) {
        ejecutar(rfc, manager -> {
            Ticket ticket = ticketAsignado(manager, rfc, idTicket, true);
            if (!"En atención".equals(ticket.getEstado())) {
                throw new IllegalArgumentException("Solo se pueden registrar observaciones mientras el ticket esté En atención.");
            }
            registrarSeguimiento(manager, ticket,
                    validarTexto(descripcion, "La observación no puede estar vacía."), false);
            return null;
        });
    }

    private Ticket ticketAsignado(EntityManager manager, String rfc, Integer idTicket, boolean bloquear) {
        if (idTicket == null || idTicket <= 0) {
            throw new IllegalArgumentException("Selecciona un ticket válido.");
        }
        Ticket ticket = bloquear
                ? manager.find(Ticket.class, idTicket, LockModeType.PESSIMISTIC_WRITE)
                : manager.find(Ticket.class, idTicket);
        if (ticket == null || ticket.getTecnico() == null || !rfc.equals(ticket.getTecnico().getRfc())) {
            throw new IllegalArgumentException("El ticket no está disponible o no está asignado a tu cuenta.");
        }
        return ticket;
    }

    private Ticket detalle(EntityManager manager, Integer idTicket) {
        return manager.createQuery(
                        "SELECT t FROM Ticket t JOIN FETCH t.equipo "
                                + "JOIN FETCH t.tipoIncidencia JOIN FETCH t.tecnico "
                                + "WHERE t.idTicket = :id", Ticket.class)
                .setParameter("id", idTicket).getSingleResult();
    }

    private void registrarSeguimiento(EntityManager manager, Ticket ticket, String texto, boolean solucionFinal) {
        Integer siguiente = manager.createQuery(
                        "SELECT COALESCE(MAX(s.id.numeroSeguimiento), 0) + 1 FROM Seguimiento s "
                                + "WHERE s.id.idTicket = :id", Integer.class)
                .setParameter("id", ticket.getIdTicket()).getSingleResult();
        Seguimiento seguimiento = new Seguimiento();
        seguimiento.setId(new SeguimientoId(ticket.getIdTicket(), siguiente));
        seguimiento.setTicket(ticket);
        seguimiento.setFecha(new Date());
        seguimiento.setDescripcion(texto);
        seguimiento.setEsSolucionFinal(solucionFinal);
        manager.persist(seguimiento);
    }

    private String validarTexto(String texto, String mensajeVacio) {
        if (texto == null || texto.isBlank()) {
            throw new IllegalArgumentException(mensajeVacio);
        }
        String normalizado = texto.strip();
        if (normalizado.length() > 4000) {
            throw new IllegalArgumentException("El texto admite hasta 4000 caracteres.");
        }
        return normalizado;
    }

    private <T> T ejecutar(String rfc, Function<EntityManager, T> operacion) {
        if (rfc == null || rfc.isBlank()) {
            throw new IllegalArgumentException("Inicia sesión para atender un ticket.");
        }
        EntityManager manager = entityManagers.get();
        EntityTransaction transaccion = manager.getTransaction();
        try {
            transaccion.begin();
            Usuario actor = manager.find(Usuario.class, rfc);
            if (actor == null || !actor.tienePermiso("atender_tickets")) {
                throw new IllegalArgumentException("No tienes permiso para atender tickets.");
            }
            T resultado = operacion.apply(manager);
            transaccion.commit();
            return resultado;
        } catch (RuntimeException error) {
            if (transaccion.isActive()) {
                transaccion.rollback();
            }
            throw error;
        } finally {
            manager.close();
        }
    }
}