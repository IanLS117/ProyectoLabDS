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
import mx.desarrollo.entity.Ticket;
import mx.desarrollo.entity.TipoIncidencia;
import mx.desarrollo.facade.FacadeEquipo;
import mx.desarrollo.facade.FacadeTicket;

@Named("ticketUI")
@ViewScoped
public class TicketBeanUI implements Serializable {

    private List<Equipo> equipos = List.of();
    private List<TipoIncidencia> tipos = List.of();
    private String numeroInventario;
    private Integer idTipoIncidencia;
    private String descripcion;
    private Ticket ticketRegistrado;
    private boolean cargaCorrecta;

    public void cargar() {
        try {
            equipos = new FacadeEquipo().obtenerEquipos(SesionActual.rfc());
            tipos = new FacadeTicket().obtenerTipos();
            cargaCorrecta = true;
        } catch (IllegalArgumentException error) {
            mensaje(FacesMessage.SEVERITY_WARN, error.getMessage());
        } catch (RuntimeException error) {
            Logger.getLogger(TicketBeanUI.class.getName()).log(Level.SEVERE, "Error al preparar ticket", error);
            mensaje(FacesMessage.SEVERITY_ERROR, "No fue posible cargar el formulario. Intenta más tarde.");
        }
    }

    public synchronized void registrar() {
        if (ticketRegistrado != null) {
            return;
        }
        try {
            ticketRegistrado = new FacadeTicket().registrarTicket(
                    SesionActual.rfc(), numeroInventario, idTipoIncidencia, descripcion);
            mensaje(FacesMessage.SEVERITY_INFO, "El ticket se registró correctamente.");
        } catch (IllegalArgumentException error) {
            mensaje(FacesMessage.SEVERITY_WARN, error.getMessage());
        } catch (RuntimeException error) {
            Logger.getLogger(TicketBeanUI.class.getName()).log(Level.SEVERE, "Error al registrar ticket", error);
            mensaje(FacesMessage.SEVERITY_ERROR, "No fue posible guardar el ticket. Intenta nuevamente.");
        }
    }

    private void mensaje(FacesMessage.Severity severidad, String texto) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severidad, texto, null));
    }

    public boolean isPuedeRegistrar() {
        return cargaCorrecta && !equipos.isEmpty() && !tipos.isEmpty() && ticketRegistrado == null;
    }

    public List<Equipo> getEquipos() { return equipos; }
    public List<TipoIncidencia> getTipos() { return tipos; }
    public boolean isCargaCorrecta() { return cargaCorrecta; }
    public Ticket getTicketRegistrado() { return ticketRegistrado; }
    public String getNumeroInventario() { return numeroInventario; }
    public void setNumeroInventario(String numeroInventario) { this.numeroInventario = numeroInventario; }
    public Integer getIdTipoIncidencia() { return idTipoIncidencia; }
    public void setIdTipoIncidencia(Integer idTipoIncidencia) { this.idTipoIncidencia = idTipoIncidencia; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
}
