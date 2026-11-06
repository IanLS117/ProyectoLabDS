package mx.desarrollo.delegate;

import java.util.Locale;
import mx.desarrollo.entity.Usuario;
import mx.desarrollo.persistence.integration.ServiceLocator;

public class DelegateUsuario {

    public Usuario login(String contrasena, String rfc) {

        if (rfc == null || rfc.isBlank()
                || contrasena == null || contrasena.isEmpty()) {
            return null;
        }

        String rfcNormalizado = rfc.trim().toUpperCase(Locale.ROOT);

        Usuario usuario = ServiceLocator
                .getInstanceUsuarioDAO()
                .buscarPorRfc(rfcNormalizado);

        if (usuario == null) {
            return null;
        }

        boolean contrasenaCorrecta = PasswordHash.verificar(
                contrasena,
                usuario.getContrasena()
        );

        if (!contrasenaCorrecta) {
            return null;
        }

        // Evitamos conservar el hash en el usuario que irá a la sesion
        usuario.setContrasena(null);

        return usuario;
    }

    public void saveUsario(Usuario usuario) {
        ServiceLocator.getInstanceUsuarioDAO().save(usuario);
    }
}