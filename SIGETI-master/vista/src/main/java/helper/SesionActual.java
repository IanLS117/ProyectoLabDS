package helper;

import jakarta.faces.context.FacesContext;
import mx.desarrollo.entity.Usuario;

public class SesionActual {

    public static String rfc() {
        Usuario usuario = (Usuario) FacesContext.getCurrentInstance()
                .getExternalContext().getSessionMap().get("usuarioAutenticado");
        if (usuario == null) {
            throw new IllegalArgumentException("La sesión terminó. Vuelve a iniciar sesión.");
        }
        return usuario.getRfc();
    }
}
