package ui;

import helper.SesionActual;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import mx.desarrollo.entity.Equipo;
import mx.desarrollo.facade.FacadeEquipo;

@Named("equipoUI")
@ViewScoped
public class EquipoBeanUI implements Serializable {

    private List<Equipo> equipos = List.of();
    private boolean cargaCorrecta;

    public void cargar() {
        try {
            equipos = new FacadeEquipo().obtenerEquipos(SesionActual.rfc());
            cargaCorrecta = true;
        } catch (IllegalArgumentException error) {
            mensaje(FacesMessage.SEVERITY_WARN, error.getMessage());
        } catch (RuntimeException error) {
            Logger.getLogger(EquipoBeanUI.class.getName()).log(Level.SEVERE, "Error al consultar equipos", error);
            mensaje(FacesMessage.SEVERITY_ERROR, "No fue posible consultar los equipos. Intenta más tarde.");
        }
    }

    private void mensaje(FacesMessage.Severity severidad, String texto) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severidad, texto, null));
    }

    public List<Equipo> getEquipos() { return equipos; }
    public boolean isCargaCorrecta() { return cargaCorrecta; }
}
