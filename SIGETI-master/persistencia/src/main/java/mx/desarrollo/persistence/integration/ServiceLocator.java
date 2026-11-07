package mx.desarrollo.persistence.integration;

import mx.desarrollo.persistence.dao.UsuarioDAO;

public class ServiceLocator {

    private static final UsuarioDAO usuarioDAO = new UsuarioDAO();

    public static UsuarioDAO getInstanceUsuarioDAO() {
        return usuarioDAO;
    }
}
