/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ui;

import helper.LoginHelper;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpServletRequest;
import mx.desarrollo.entity.Usuario;
import java.util.logging.Level;
import java.util.logging.Logger;

@Named("loginUI")
@RequestScoped
public class LoginBeanUI {

    private String rfc;
    private String contrasena;

    public String login() {
        FacesContext contexto = FacesContext.getCurrentInstance();

        if (rfc == null || rfc.isBlank()
                || contrasena == null || contrasena.isEmpty()) {
            contrasena = null;

            contexto.addMessage(null, new FacesMessage(
                    FacesMessage.SEVERITY_WARN,
                    "Completa el RFC y la contraseña.",
                    null
            ));

            return null;
        }

        try {
            LoginHelper loginHelper = new LoginHelper();
            Usuario usuario = loginHelper.login(rfc, contrasena);

            if (usuario == null) {
                contexto.addMessage(null, new FacesMessage(
                        FacesMessage.SEVERITY_ERROR,
                        "RFC o contraseña incorrectos.",
                        "Intenta nuevamente."
                ));

                return null;
            }

            HttpServletRequest solicitud =
                    (HttpServletRequest) contexto
                            .getExternalContext()
                            .getRequest();

            solicitud.getSession(true);
            solicitud.changeSessionId();

            contexto.getExternalContext()
                    .getSessionMap()
                    .put("usuarioAutenticado", usuario);

            return "/index.xhtml?faces-redirect=true";

        } catch (RuntimeException e) {
            Logger.getLogger(LoginBeanUI.class.getName())
                    .log(Level.SEVERE, "Error al iniciar sesión", e);

            contexto.addMessage(null, new FacesMessage(
                    FacesMessage.SEVERITY_ERROR,
                    "No fue posible acceder al sistema.",
                    "Intenta más tarde."
            ));

            return null;

        } finally {
            contrasena = null;
        }
    }

    public String cerrarSesion() {
        FacesContext.getCurrentInstance()
                .getExternalContext()
                .invalidateSession();

        return "/login.xhtml?faces-redirect=true";
    }

    public Usuario getUsuario() {
        return (Usuario) FacesContext.getCurrentInstance()
                .getExternalContext()
                .getSessionMap()
                .get("usuarioAutenticado");
    }

    public String getRfc() {
        return rfc;
    }

    public void setRfc(String rfc) {
        this.rfc = rfc;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }
}
