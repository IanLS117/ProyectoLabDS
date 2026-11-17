package mx.desarrollo.persistence.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import mx.desarrollo.entity.Usuario;
import mx.desarrollo.persistence.persistence.AbstractDAO;
import mx.desarrollo.persistence.persistence.HibernateUtil;
import java.util.List;

public class UsuarioDAO extends AbstractDAO<Usuario> {

    public UsuarioDAO() {
        super(Usuario.class);
    }

    public List<Usuario> obtenerTodos() {
        return findAll();
    }

    public Usuario buscarPorRfc(String rfc) {
        return execute(em -> {
            String consulta =
                    "SELECT DISTINCT u FROM Usuario u "
                            + "LEFT JOIN FETCH u.rol r "
                            + "LEFT JOIN FETCH r.permisos "
                            + "WHERE u.rfc = :rfc";

            List<Usuario> usuarios = em
                    .createQuery(consulta, Usuario.class)
                    .setParameter("rfc", rfc)
                    .getResultList();

            if (usuarios.isEmpty()) {
                return null;
            }

            return usuarios.get(0);
        });
    }

    @Override
    protected EntityManager getEntityManager() {
        return HibernateUtil.getEntityManager();
    }

    public List<Usuario> obtenerSoportesTecnicos() {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Usuario> query = em.createQuery(
                    "SELECT u FROM Usuario u " +
                            "WHERE u.rol.idRol = 2 " +
                            "ORDER BY u.nombre ASC, u.apellidos ASC",
                    Usuario.class
            );
            return query.getResultList();
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }
}
