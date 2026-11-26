package ui;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
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

    private List<Ticket> todosLosTickets = new ArrayList<>();
    private List<Ticket> tickets = new ArrayList<>();
    private List<Ticket> ticketsFiltrados;
    private Ticket ticketSeleccionado;

    // --- Filtros ---
    private String prioridadFiltro;
    private LocalDate fechaFiltro;
    private String estadoFiltro;

    private long totalTickets;
    private long totalEnviados;
    private long totalEnAtencion;
    private long totalResueltos;

    // --- Reasignación de tickets ---
    private Ticket ticketAReasignar;
    private String rfcNuevoSoporte;
    private List<Usuario> listaSoportes = new ArrayList<>();
    private Map<String, Long> cargaPorSoporte = new HashMap<>();
    private boolean mostrarModalReasignacion;

    // --- Cambio de prioridad ---
    private Ticket ticketPrioridad;
    private String nuevaPrioridad;

    // --- Área de experto de soporte ---
    private String rfcSoporteArea;
    private String areaExpertoEdit;

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
            todosLosTickets = delegateTicket.obtenerTodosAdmin(usuario.getRfc());
            calcularResumen();
            aplicarFiltros();
        } catch (Exception e) {
            todosLosTickets = new ArrayList<>();
            tickets = new ArrayList<>();
            ticketsFiltrados = null;
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Error al consultar tickets", e.getMessage()));
        }
    }

    public void aplicarFiltros() {
        if (todosLosTickets == null) {
            tickets = new ArrayList<>();
            ticketsFiltrados = null;
            return;
        }
        tickets = todosLosTickets.stream()
                .filter(t -> prioridadFiltro == null || prioridadFiltro.isBlank()
                        || prioridadFiltro.equalsIgnoreCase(t.getPrioridad()))
                .filter(t -> estadoFiltro == null || estadoFiltro.isBlank()
                        || estadoFiltro.equalsIgnoreCase(t.getEstado()))
                .filter(t -> fechaFiltro == null
                        || (t.getFechaCreacion() != null && t.getFechaCreacion().toLocalDate().equals(fechaFiltro)))
                .collect(Collectors.toList());
        ticketsFiltrados = null;
    }

    public void limpiarFiltros() {
        this.prioridadFiltro = null;
        this.fechaFiltro = null;
        this.estadoFiltro = null;
        aplicarFiltros();
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
        this.rfcNuevoSoporte = (ticket != null && ticket.getTecnico() != null)
                ? ticket.getTecnico().getRfc()
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

    public void abrirModalPrioridad(Ticket ticket) {
        this.ticketPrioridad = ticket;
        this.nuevaPrioridad = ticket != null ? ticket.getPrioridad() : "Media";
    }

    public void confirmarCambioPrioridad() {
        FacesContext context = FacesContext.getCurrentInstance();
        try {
            Usuario adminActual = obtenerUsuarioSesion();
            if (adminActual == null || ticketPrioridad == null) {
                throw new IllegalStateException("No hay un ticket seleccionado.");
            }
            delegateTicket.cambiarPrioridadTicket(ticketPrioridad.getIdTicket(), nuevaPrioridad, adminActual);
            cargarTickets();
            context.addMessage(null, new FacesMessage(
                    FacesMessage.SEVERITY_INFO,
                    "Prioridad actualizada",
                    "El cambio quedó registrado en la bitácora."
            ));
        } catch (Exception ex) {
            context.addMessage(null, new FacesMessage(
                    FacesMessage.SEVERITY_ERROR,
                    "No se pudo cambiar la prioridad",
                    ex.getMessage()
            ));
        }
    }

    public void abrirModalAreaExperto() {
        cargarDatosReasignacion();
        this.rfcSoporteArea = null;
        this.areaExpertoEdit = null;
    }

    public void onCambioSoporteArea() {
        this.areaExpertoEdit = null;
        if (rfcSoporteArea == null || listaSoportes == null) {
            return;
        }
        for (Usuario u : listaSoportes) {
            if (rfcSoporteArea.equals(u.getRfc())) {
                this.areaExpertoEdit = u.getAreaExperto();
                break;
            }
        }
    }

    public void guardarAreaExperto() {
        FacesContext context = FacesContext.getCurrentInstance();
        try {
            delegateUsuario.actualizarAreaExperto(rfcSoporteArea, areaExpertoEdit);
            cargarDatosReasignacion();
            cargarTickets();
            context.addMessage(null, new FacesMessage(
                    FacesMessage.SEVERITY_INFO,
                    "Área de experto actualizada",
                    "La especialidad del técnico fue guardada correctamente."
            ));
        } catch (Exception ex) {
            context.addMessage(null, new FacesMessage(
                    FacesMessage.SEVERITY_ERROR,
                    "No se pudo guardar el área",
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
        totalTickets = todosLosTickets.size();
        totalEnviados = todosLosTickets.stream()
                .filter(t -> "Enviado".equalsIgnoreCase(t.getEstado()))
                .count();
        totalEnAtencion = todosLosTickets.stream()
                .filter(t -> "En atención".equalsIgnoreCase(t.getEstado())
                        || "En atencion".equalsIgnoreCase(t.getEstado()))
                .count();
        totalResueltos = todosLosTickets.stream()
                .filter(t -> "Terminado".equalsIgnoreCase(t.getEstado())
                        || "Resuelto".equalsIgnoreCase(t.getEstado())
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

    public List<Ticket> getTickets() { return tickets; }

    public List<Ticket> getTicketsFiltrados() { return ticketsFiltrados; }

    public void setTicketsFiltrados(List<Ticket> ticketsFiltrados) { this.ticketsFiltrados = ticketsFiltrados; }

    public Ticket getTicketSeleccionado() { return ticketSeleccionado; }

    public void setTicketSeleccionado(Ticket ticketSeleccionado) { this.ticketSeleccionado = ticketSeleccionado; }

    public String getPrioridadFiltro() { return prioridadFiltro; }

    public void setPrioridadFiltro(String prioridadFiltro) { this.prioridadFiltro = prioridadFiltro; }

    public LocalDate getFechaFiltro() { return fechaFiltro; }

    public void setFechaFiltro(LocalDate fechaFiltro) { this.fechaFiltro = fechaFiltro; }

    public String getEstadoFiltro() { return estadoFiltro; }

    public void setEstadoFiltro(String estadoFiltro) { this.estadoFiltro = estadoFiltro; }

    public long getTotalTickets() { return totalTickets; }

    public long getTotalEnviados() { return totalEnviados; }

    public long getTotalEnAtencion() { return totalEnAtencion; }

    public long getTotalResueltos() { return totalResueltos; }

    public Ticket getTicketAReasignar() { return ticketAReasignar; }

    public void setTicketAReasignar(Ticket ticketAReasignar) { this.ticketAReasignar = ticketAReasignar; }

    public String getRfcNuevoSoporte() { return rfcNuevoSoporte; }

    public void setRfcNuevoSoporte(String rfcNuevoSoporte) { this.rfcNuevoSoporte = rfcNuevoSoporte; }

    public List<Usuario> getListaSoportes() { return listaSoportes; }

    public Map<String, Long> getCargaPorSoporte() { return cargaPorSoporte; }

    public boolean isMostrarModalReasignacion() { return mostrarModalReasignacion; }

    public Ticket getTicketPrioridad() { return ticketPrioridad; }

    public String getNuevaPrioridad() { return nuevaPrioridad; }

    public void setNuevaPrioridad(String nuevaPrioridad) { this.nuevaPrioridad = nuevaPrioridad; }

    public String getRfcSoporteArea() { return rfcSoporteArea; }

    public void setRfcSoporteArea(String rfcSoporteArea) { this.rfcSoporteArea = rfcSoporteArea; }

    public String getAreaExpertoEdit() { return areaExpertoEdit; }

    public void setAreaExpertoEdit(String areaExpertoEdit) { this.areaExpertoEdit = areaExpertoEdit; }
}