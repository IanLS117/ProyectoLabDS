package mx.desarrollo.persistence.dao;

import jakarta.persistence.EntityManager;
import java.util.List;
import mx.desarrollo.entity.TipoIncidencia;
import mx.desarrollo.persistence.persistence.HibernateUtil;

public class TipoIncidenciaDAO {

    public List<TipoIncidencia> obtenerActivos() {
        EntityManager manager = HibernateUtil.getEntityManager();
        try {
            return manager.createQuery(
                    "SELECT tipo FROM TipoIncidencia tipo WHERE tipo.activo = true ORDER BY tipo.nombre",
                    TipoIncidencia.class).getResultList();
        } finally {
            manager.close();
        }
    }
}
