package helper;

import mx.desarrollo.delegate.DelegateUsuario;
import mx.desarrollo.entity.Usuario;

public class LoginHelper {
    private DelegateUsuario delegateUsuario = new DelegateUsuario();

    public Usuario Login(String rfc, String contrasena) {
        if (rfc == null || rfc.trim().isEmpty() || contrasena == null || contrasena.trim().isEmpty()) {
            return null;
        }
        return delegateUsuario.login(rfc, contrasena);
    }
}