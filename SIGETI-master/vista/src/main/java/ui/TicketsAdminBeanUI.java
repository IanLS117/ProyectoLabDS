package ui;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import mx.desarrollo.delegate.DelegateTicket;
import mx.desarrollo.entity.Ticket;
import mx.desarrollo.entity.Usuario;

@Named("ticketsAdminUI")
@ViewScoped
public class TicketsAdminBeanUI implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final DelegateTicket delegateTicket = new DelegateTicket();

    private List<Ticket> tickets = new ArrayList<>();
    private List<Ticket> ticketsFiltrados;
    private Ticket ticketSeleccionado;

    private long totalTickets;
    private long totalEnviados;
    private long totalEnAtencion;
    private long totalResueltos;

    @PostConstruct
    public void init() {
        cargarTickets();
    }

    public void cargarTickets() {
        try {
            Usuario usuario = obtenerUsuarioSesion();
            if (usuario == null) {
                return;
            }
            tickets = delegateTicket.obtenerTodosAdmin(usuario.getRfc());
            ticketsFiltrados = null;
            calcularResumen();
        } catch (Exception e) {
            tickets = new ArrayList<>();
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Error al consultar tickets", e.getMessage()));
        }
    }

    private void calcularResumen() {
        totalTickets = tickets.size();
        totalEnviados = tickets.stream()
                .filter(t -> "Enviado".equalsIgnoreCase(t.getEstado()))
                .count();
        totalEnAtencion = tickets.stream()
                .filter(t -> "En atención".equalsIgnoreCase(t.getEstado())
                        || "En atencion".equalsIgnoreCase(t.getEstado()))
                .count();
        totalResueltos = tickets.stream()
                .filter(t -> "Resuelto".equalsIgnoreCase(t.getEstado())
                        || "Cerrado".equalsIgnoreCase(t.getEstado()))
                .count();
    }

    public void seleccionarTicket(Ticket ticket) {
        this.ticketSeleccionado = ticket;
    }

    public String formatearFecha(LocalDateTime fecha) {
        return fecha != null ? fecha.format(FORMATO_FECHA) : "-";
    }

    private Usuario obtenerUsuarioSesion() {
        FacesContext context = FacesContext.getCurrentInstance();
        if (context == null) {
            return null;
        }
        return (Usuario) context.getExternalContext().getSessionMap().get("usuarioAutenticado");
    }

    public List<Ticket> getTickets() {
        return tickets;
    }

    public List<Ticket> getTicketsFiltrados() {
        return ticketsFiltrados;
    }

    public void setTicketsFiltrados(List<Ticket> ticketsFiltrados) {
        this.ticketsFiltrados = ticketsFiltrados;
    }

    public Ticket getTicketSeleccionado() {
        return ticketSeleccionado;
    }

    public void setTicketSeleccionado(Ticket ticketSeleccionado) {
        this.ticketSeleccionado = ticketSeleccionado;
    }

    public long getTotalTickets() {
        return totalTickets;
    }

    public long getTotalEnviados() {
        return totalEnviados;
    }

    public long getTotalEnAtencion() {
        return totalEnAtencion;
    }

    public long getTotalResueltos() {
        return totalResueltos;
    }
}