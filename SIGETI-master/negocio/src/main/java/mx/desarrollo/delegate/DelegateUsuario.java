package mx.desarrollo.delegate;

import mx.desarrollo.entity.Usuario;
import mx.desarrollo.integration.ServiceFacadeLocator;

public class DelegateUsuario {

    public Usuario login(String rfc, String contrasena) {
        return ServiceFacadeLocator.getInstanceFacadeUsuario().login(rfc, contrasena);
    }
}