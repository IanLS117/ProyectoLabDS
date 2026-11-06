package mx.desarrollo.persistence.dao;

import jakarta.persistence.EntityManager;
import mx.desarrollo.entity.Usuario;
import mx.desarrollo.persistence.persistence.AbstractDAO;
import mx.desarrollo.persistence.persistence.HibernateUtil;

import java.util.List;

public class UsuarioDAO extends AbstractDAO<Usuario> {
    private EntityManager entityManager;

    // Constructor sin parámetros para ServiceLocator
    public UsuarioDAO() {
        super(Usuario.class);
        this.entityManager = HibernateUtil.getEntityManager();
    }

    // Constructor con parámetro para inyección o pruebas manuales
    public UsuarioDAO(EntityManager em) {
        super(Usuario.class);
        this.entityManager = em;
    }

    public List<Usuario> obtenerTodos(){
        return getEntityManager()
                .createQuery("SELECT u FROM Usuario u", Usuario.class)
                .getResultList();
    }

    @Override
    public EntityManager getEntityManager() {
        if (this.entityManager == null || !this.entityManager.isOpen()) {
            this.entityManager = HibernateUtil.getEntityManager();
        }
        return this.entityManager;
    }
}