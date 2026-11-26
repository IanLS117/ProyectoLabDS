package mx.desarrollo.delegate;

import java.util.List;
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

        // aqui se evita conservar el hash en el usuario que irá a la sesion
        usuario.setContrasena(null);

        return usuario;
    }

    public void actualizarAreaExperto(String rfcSoporte, String areaExperto) {
        if (rfcSoporte == null || rfcSoporte.isBlank()) {
            throw new IllegalArgumentException("Selecciona un técnico de soporte válido.");
        }
        var usuarioDAO = ServiceLocator.getInstanceUsuarioDAO();
        Usuario soporte = usuarioDAO.find(rfcSoporte)
                .orElseThrow(() -> new IllegalArgumentException("No se encontró el técnico de soporte."));

        soporte.setAreaExperto(areaExperto != null ? areaExperto.trim() : null);
        usuarioDAO.update(soporte);
    }

    public void saveUsario(Usuario usuario) {
        ServiceLocator.getInstanceUsuarioDAO().save(usuario);
    }

    public List<Usuario> listarSoportesTecnicos() {
        return ServiceLocator.getInstanceUsuarioDAO().obtenerSoportesTecnicos();
    }
}