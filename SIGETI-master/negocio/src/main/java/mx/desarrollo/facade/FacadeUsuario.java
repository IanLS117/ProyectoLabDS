package mx.desarrollo.facade;

import mx.desarrollo.delegate.DelegateUsuario;
import mx.desarrollo.entity.Usuario;

public class FacadeUsuario {

    private final DelegateUsuario delegateUsuario;

    public FacadeUsuario() {
        this.delegateUsuario = new DelegateUsuario();
    }

    public Usuario login(String contrasena, String rfc) {
        return delegateUsuario.login(contrasena, rfc);
    }

    public void saveUsario(Usuario usuario) {
        delegateUsuario.saveUsario(usuario);
    }
}