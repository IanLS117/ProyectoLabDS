package mx.desarrollo.persistence.integration;

import mx.desarrollo.persistence.dao.SeguimientoDAO;
import mx.desarrollo.persistence.dao.UsuarioDAO;

public class ServiceLocator {

    private static final UsuarioDAO usuarioDAO = new UsuarioDAO();

    public static UsuarioDAO getInstanceUsuarioDAO() {
        return usuarioDAO;
    }

    private static SeguimientoDAO seguimientoDAO;

    public static SeguimientoDAO getInstanceSeguimientoDAO() {
        if (seguimientoDAO == null) {
            seguimientoDAO = new SeguimientoDAO();
        }
        return seguimientoDAO;
    }
}
