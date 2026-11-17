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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import mx.desarrollo.delegate.DelegateTicket;
import mx.desarrollo.delegate.DelegateUsuario;
import mx.desarrollo.entity.Ticket;
import mx.desarrollo.entity.Usuario;

@Named("ticketsAdminUI")
@ViewScoped
public class TicketsAdminBeanUI implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final DelegateTicket delegateTicket = new DelegateTicket();
    private final DelegateUsuario delegateUsuario = new DelegateUsuario();

    private List<Ticket> tickets = new ArrayList<>();
    private List<Ticket> ticketsFiltrados;
    private Ticket ticketSeleccionado;

    private long totalTickets;
    private long totalEnviados;
    private long totalEnAtencion;
    private long totalResueltos;

    // --- Atributos para reasignación de tickets ---
    private Ticket ticketAReasignar;
    private String rfcNuevoSoporte;
    private List<Usuario> listaSoportes = new ArrayList<>();
    private Map<String, Long> cargaPorSoporte = new HashMap<>();
    private boolean mostrarModalReasignacion;

    @PostConstruct
    public void init() {
        cargarTickets();
        cargarDatosReasignacion();
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

    public void cargarDatosReasignacion() {
        try {
            this.listaSoportes = delegateUsuario.listarSoportesTecnicos();
            this.cargaPorSoporte = delegateTicket.obtenerCargaTrabajoSoportes();
        } catch (Exception e) {
            this.listaSoportes = new ArrayList<>();
            this.cargaPorSoporte = new HashMap<>();
        }
    }

    public void abrirModalReasignacion(Ticket ticket) {
        this.ticketAReasignar = ticket;
        this.rfcNuevoSoporte = (ticket != null && ticket.getSoporteAsignado() != null)
                ? ticket.getSoporteAsignado().getRfc()
                : null;
        cargarDatosReasignacion();
        this.mostrarModalReasignacion = true;
    }

    public void cerrarModalReasignacion() {
        this.mostrarModalReasignacion = false;
        this.ticketAReasignar = null;
        this.rfcNuevoSoporte = null;
    }

    public void confirmarReasignacion() {
        FacesContext context = FacesContext.getCurrentInstance();
        try {
            Usuario adminActual = obtenerUsuarioSesion();
            if (adminActual == null) {
                throw new IllegalStateException("La sesión ha expirado. Vuelva a iniciar sesión.");
            }
            if (ticketAReasignar == null) {
                throw new IllegalArgumentException("No hay un ticket seleccionado para reasignar.");
            }

            delegateTicket.reasignarTicket(ticketAReasignar.getIdTicket(), rfcNuevoSoporte, adminActual);

            cargarTickets();
            cargarDatosReasignacion();
            cerrarModalReasignacion();

            context.addMessage(null, new FacesMessage(
                    FacesMessage.SEVERITY_INFO,
                    "Ticket reasignado",
                    "La carga de trabajo fue actualizada correctamente."
            ));
        } catch (Exception ex) {
            context.addMessage(null, new FacesMessage(
                    FacesMessage.SEVERITY_ERROR,
                    "No se pudo reasignar",
                    ex.getMessage()
            ));
        }
    }

    public long obtenerCargaDeSoporte(String rfcSoporte) {
        if (cargaPorSoporte == null || rfcSoporte == null) {
            return 0L;
        }
        return cargaPorSoporte.getOrDefault(rfcSoporte, 0L);
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

    public long getTotalResueltos() { return totalResueltos; }

    public Ticket getTicketAReasignar() { return ticketAReasignar; }

    public void setTicketAReasignar(Ticket ticketAReasignar) { this.ticketAReasignar = ticketAReasignar; }

    public String getRfcNuevoSoporte() { return rfcNuevoSoporte; }

    public void setRfcNuevoSoporte(String rfcNuevoSoporte) { this.rfcNuevoSoporte = rfcNuevoSoporte; }

    public List<Usuario> getListaSoportes() { return listaSoportes; }

    public Map<String, Long> getCargaPorSoporte() { return cargaPorSoporte; }

    public boolean isMostrarModalReasignacion() { return mostrarModalReasignacion; }
}