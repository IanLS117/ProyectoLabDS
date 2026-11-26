package ui;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import java.io.Serializable;
import mx.desarrollo.entity.Usuario;

@Named("perfilUI")
@ViewScoped
public class PerfilBeanUI implements Serializable {

    private static final long serialVersionUID = 1L;
    private Usuario usuario;

    @PostConstruct
    public void init() {
        FacesContext context = FacesContext.getCurrentInstance();
        if (context != null) {
            this.usuario = (Usuario) context.getExternalContext()
                    .getSessionMap().get("usuarioAutenticado");
        }
    }

    public void actualizarPerfil() {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO,
                        "Información verificada",
                        "Tus datos de perfil están actualizados."));
    }

    public Usuario getUsuario() {
        return usuario;
    }
}