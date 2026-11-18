package ui;

import helper.SesionActual;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import mx.desarrollo.delegate.DelegateSeguimiento;
import mx.desarrollo.entity.Seguimiento;
import mx.desarrollo.entity.Ticket;
import mx.desarrollo.facade.FacadeAtencionTicket;

@Named("seguimientoBeanUI")
@ViewScoped
public class SeguimientoBeanUI implements Serializable {

    private static final long serialVersionUID = 1L;
    private List<Ticket> ticketsAsignados = List.of();
    private Ticket ticketSeleccionado;
    private List<Seguimiento> historialSeguimientos = List.of();
    private String nuevaObservacion = "";
    private String solucion = "";

    @PostConstruct
    public void init() {
        cargarTickets();
    }

    public void cargarTickets() {
        try {
            ticketsAsignados = new FacadeAtencionTicket().listarAsignados(SesionActual.rfc());
            if (ticketSeleccionado != null) {
                cargarDetalle(ticketSeleccionado.getIdTicket());
            }
        } catch (IllegalArgumentException error) {
            limpiarSeleccion();
            ticketsAsignados = List.of();
            mensaje(FacesMessage.SEVERITY_WARN, error.getMessage());
        } catch (RuntimeException error) {
            limpiarSeleccion();
            ticketsAsignados = List.of();
            errorTecnico("Error al consultar tickets asignados", error);
        }
    }

    public void seleccionarTicket(Ticket ticket) {
        try {
            if (ticket == null) {
                throw new IllegalArgumentException("Selecciona un ticket válido.");
            }
            cargarDetalle(ticket.getIdTicket());
            nuevaObservacion = "";
            solucion = "";
        } catch (IllegalArgumentException error) {
            limpiarSeleccion();
            mensaje(FacesMessage.SEVERITY_WARN, error.getMessage());
        } catch (RuntimeException error) {
            limpiarSeleccion();
            errorTecnico("Error al consultar el detalle del ticket", error);
        }
    }

    private void cargarDetalle(Integer idTicket) {
        String rfc = SesionActual.rfc();
        Ticket detalle = new FacadeAtencionTicket().consultar(rfc, idTicket);
        List<Seguimiento> historial = new DelegateSeguimiento().consultarPorTicket(rfc, idTicket);
        ticketSeleccionado = detalle;
        historialSeguimientos = historial;
    }

    public void iniciarAtencion() {
        cambiarEstado("En atención", null);
    }

    public void terminarTicket() {
        cambiarEstado("Terminado", solucion);
    }

    private void cambiarEstado(String destino, String textoSolucion) {
        try {
            Integer id = idSeleccionado();
            new FacadeAtencionTicket().cambiarEstado(SesionActual.rfc(), id,
                    ticketSeleccionado.getEstado(), destino, textoSolucion);
            nuevaObservacion = "";
            solucion = "";
            cargarTickets();
            mensaje(FacesMessage.SEVERITY_INFO, "Estado guardado: " + destino + ".");
        } catch (IllegalArgumentException error) {
            mensaje(FacesMessage.SEVERITY_WARN,
                    "Terminado".equals(destino) ? "No se pudo terminar el ticket" : "No se pudo cambiar el estado",
                    error.getMessage());
        } catch (RuntimeException error) {
            errorTecnico("Error al cambiar el estado del ticket", error);
        }
    }

    public void guardarObservacion() {
        try {
            Integer id = idSeleccionado();
            new DelegateSeguimiento().registrarObservacion(SesionActual.rfc(), id, nuevaObservacion);
            nuevaObservacion = "";
            cargarDetalle(id);
            mensaje(FacesMessage.SEVERITY_INFO, "Observación guardada en el historial del ticket.");
        } catch (IllegalArgumentException error) {
            mensaje(FacesMessage.SEVERITY_WARN, error.getMessage());
        } catch (RuntimeException error) {
            errorTecnico("Error al guardar la observación", error);
        }
    }

    private Integer idSeleccionado() {
        if (ticketSeleccionado == null) {
            throw new IllegalArgumentException("Selecciona un ticket válido.");
        }
        return ticketSeleccionado.getIdTicket();
    }

    private void limpiarSeleccion() {
        ticketSeleccionado = null;
        historialSeguimientos = List.of();
        nuevaObservacion = "";
        solucion = "";
    }

    private void errorTecnico(String texto, RuntimeException error) {
        Logger.getLogger(SeguimientoBeanUI.class.getName()).log(Level.SEVERE, texto, error);
        mensaje(FacesMessage.SEVERITY_ERROR, "No fue posible completar la operación. Intenta más tarde.");
    }

    private void mensaje(FacesMessage.Severity severidad, String texto) {
        String titulo = FacesMessage.SEVERITY_INFO.equals(severidad) ? "Operación realizada"
                : FacesMessage.SEVERITY_WARN.equals(severidad) ? "Atención requerida" : "Operación no realizada";
        mensaje(severidad, titulo, texto);
    }

    private void mensaje(FacesMessage.Severity severidad, String titulo, String detalle) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severidad, titulo, detalle));
    }

    public boolean isPuedeIniciar() {
        return ticketSeleccionado != null && "Enviado".equals(ticketSeleccionado.getEstado());
    }

    public boolean isPuedeTerminar() {
        return ticketSeleccionado != null && "En atención".equals(ticketSeleccionado.getEstado());
    }

    public List<Ticket> getTicketsAsignados() { return ticketsAsignados; }
    public Ticket getTicketSeleccionado() { return ticketSeleccionado; }
    public List<Seguimiento> getHistorialSeguimientos() { return historialSeguimientos; }
    public String getNuevaObservacion() { return nuevaObservacion; }
    public void setNuevaObservacion(String nuevaObservacion) { this.nuevaObservacion = nuevaObservacion; }
    public String getSolucion() { return solucion; }
    public void setSolucion(String solucion) { this.solucion = solucion; }
}
