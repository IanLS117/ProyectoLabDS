package mx.desarrollo.persistence.dao;

import jakarta.persistence.EntityManager;
import java.util.List;
import mx.desarrollo.entity.Equipo;
import mx.desarrollo.persistence.persistence.HibernateUtil;

public class EquipoDAO {

    public List<Equipo> obtenerEquiposPorUsuario(String rfc) {
        EntityManager manager = HibernateUtil.getEntityManager();
        try {
            return manager.createQuery(
                    "SELECT equipo FROM Equipo equipo WHERE equipo.usuarioAsignado.rfcUsuario = :rfc ORDER BY equipo.numeroInventario",
                    Equipo.class).setParameter("rfc", rfc).getResultList();
        } finally {
            manager.close();
        }
    }
}
