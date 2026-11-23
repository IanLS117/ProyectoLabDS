package ui;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import mx.desarrollo.delegate.DelegateSeguimiento;
import mx.desarrollo.delegate.DelegateTicket;
import mx.desarrollo.entity.Seguimiento;
import mx.desarrollo.entity.Ticket;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Named("seguimientoBeanUI")
@ViewScoped
public class SeguimientoBeanUI implements Serializable {

    private transient DelegateSeguimiento delegateSeguimiento = new DelegateSeguimiento();
    private transient DelegateTicket delegateTicket = new DelegateTicket();

    private List<Ticket> ticketsAsignados = new ArrayList<>();
    private Ticket ticketSeleccionado;
    private List<Seguimiento> historialSeguimientos = new ArrayList<>();
    private String nuevaObservacion;

    @PostConstruct
    public void init() {
        cargarTickets();
    }

    public void cargarTickets() {
        // Reemplaza obtenerTickets() por el método que tengas en DelegateTicket
        this.ticketsAsignados = delegateTicket.obtenerTickets();
    }

    public void seleccionarTicket(Ticket ticket) {
        this.ticketSeleccionado = ticket;
        this.nuevaObservacion = "";
        cargarHistorial();
    }

    public void cargarHistorial() {
        if (ticketSeleccionado != null) {
            this.historialSeguimientos = delegateSeguimiento.consultarPorTicket(ticketSeleccionado.getIdTicket());
        } else {
            this.historialSeguimientos = new ArrayList<>();
        }
    }

    public void guardarObservacion() {
        try {
            delegateSeguimiento.registrarObservacion(ticketSeleccionado, nuevaObservacion);
            this.nuevaObservacion = "";
            cargarHistorial();
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Observación guardada", "Se registró el avance en el historial del ticket."));
        } catch (IllegalArgumentException e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_WARN, "Validación", e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            String detalle = (e.getCause() != null) ? e.getCause().getMessage() : e.getMessage();
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error al guardar", detalle));
        }
    }

    // Getters y Setters
    public List<Ticket> getTicketsAsignados() { return ticketsAsignados; }
    public Ticket getTicketSeleccionado() { return ticketSeleccionado; }
    public void setTicketSeleccionado(Ticket ticketSeleccionado) { this.ticketSeleccionado = ticketSeleccionado; }
    public List<Seguimiento> getHistorialSeguimientos() { return historialSeguimientos; }
    public String getNuevaObservacion() { return nuevaObservacion; }
    public void setNuevaObservacion(String nuevaObservacion) { this.nuevaObservacion = nuevaObservacion; }
}