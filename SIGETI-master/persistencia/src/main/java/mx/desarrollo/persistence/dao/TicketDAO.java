package mx.desarrollo.persistence.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import mx.desarrollo.entity.Equipo;
import mx.desarrollo.entity.Ticket;
import mx.desarrollo.persistence.persistence.HibernateUtil;

import java.util.ArrayList;
import java.util.List;

public class TicketDAO {

    public List<Equipo> obtenerEquiposPorUsuario(int idUsuario) {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            TypedQuery<Equipo> query = em.createQuery(
                    "SELECT e FROM Equipo e WHERE e.usuarioResguardo.id = :idUsuario",
                    Equipo.class
            );
            query.setParameter("idUsuario", idUsuario);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        } finally {
            em.close();
        }
    }

    public void guardar(Ticket ticket) throws Exception {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(ticket);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction() != null && em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }
}