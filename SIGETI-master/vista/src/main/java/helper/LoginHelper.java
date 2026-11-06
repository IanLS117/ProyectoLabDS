package helper;

import java.io.Serializable;
import mx.desarrollo.entity.Usuario;
import mx.desarrollo.integration.ServiceFacadeLocator;

public class LoginHelper implements Serializable {

    /**
     * Método para hacer login llamando a la instancia de usuarioFacade
     * @param correo Correo electrónico del usuario
     * @param password Contraseña de acceso
     * @return Usuario autenticado
     */
    public Usuario login(String correo, String password) {
        return ServiceFacadeLocator.getInstanceFacadeUsuario().login(password, correo);
    }
}