package mx.desarrollo.persistence.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import mx.desarrollo.entity.Equipo;
import mx.desarrollo.persistence.persistence.HibernateUtil;

import java.util.List;

public class EquipoDAO {

    public List<Equipo> obtenerEquiposPorUsuario(String rfcUsuario) {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            String hql = "FROM Equipo e WHERE e.idUsuarioAsignado = :rfcUsr";
            TypedQuery<Equipo> query = em.createQuery(hql, Equipo.class);
            query.setParameter("rfcUsr", rfcUsuario);
            return query.getResultList();
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }
}