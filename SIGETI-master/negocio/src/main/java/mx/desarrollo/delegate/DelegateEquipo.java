package mx.desarrollo.delegate;

import java.util.List;
import mx.desarrollo.entity.Equipo;
import mx.desarrollo.entity.Usuario;
import mx.desarrollo.persistence.dao.EquipoDAO;
import mx.desarrollo.persistence.dao.UsuarioDAO;

public class DelegateEquipo {

    public List<Equipo> obtenerEquipos(String rfc) {
        if (rfc == null || rfc.isBlank()) {
            throw new IllegalArgumentException("Inicia sesión para consultar tus equipos.");
        }
        Usuario usuario = new UsuarioDAO().buscarPorRfc(rfc);
        if (usuario == null || !usuario.tienePermiso("consultar_equipos")) {
            throw new IllegalArgumentException("No tienes permiso para consultar equipos.");
        }
        return new EquipoDAO().obtenerEquiposPorUsuario(rfc);
    }
}
