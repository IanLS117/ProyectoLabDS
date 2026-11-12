package mx.desarrollo.persistence.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import mx.desarrollo.entity.Seguimiento;
import mx.desarrollo.persistence.persistence.AbstractDAO;
import mx.desarrollo.persistence.persistence.HibernateUtil;
import java.util.List;
import mx.desarrollo.entity.Ticket;

public class SeguimientoDAO extends AbstractDAO<Seguimiento> {

    public void guardar(Seguimiento seguimiento) {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            if (seguimiento.getId() != null && seguimiento.getId().getIdTicket() != null) {
                Ticket ticketRef = em.getReference(Ticket.class, seguimiento.getId().getIdTicket());
                seguimiento.setTicket(ticketRef);
            }
            em.persist(seguimiento);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    public SeguimientoDAO() {
        super(Seguimiento.class);
    }

    @Override
    public EntityManager getEntityManager() {
        return HibernateUtil.getEntityManager();
    }

    public int obtenerSiguienteNumero(Integer idTicket) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Integer> query = em.createQuery(
                    "SELECT COALESCE(MAX(s.id.numeroSeguimiento), 0) + 1 " +
                            "FROM Seguimiento s WHERE s.id.idTicket = :idTicket",
                    Integer.class
            );
            query.setParameter("idTicket", idTicket);
            return query.getSingleResult();
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    public List<Seguimiento> listarPorTicket(Integer idTicket) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Seguimiento> query = em.createQuery(
                    "SELECT s FROM Seguimiento s " +
                            "WHERE s.id.idTicket = :idTicket " +
                            "ORDER BY s.fecha ASC, s.id.numeroSeguimiento ASC",
                    Seguimiento.class
            );
            query.setParameter("idTicket", idTicket);
            return query.getResultList();
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }
}