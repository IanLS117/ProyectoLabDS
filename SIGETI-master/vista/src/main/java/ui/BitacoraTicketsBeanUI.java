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
import java.util.List;
import java.util.stream.Collectors;
import mx.desarrollo.delegate.DelegateTicket;
import mx.desarrollo.entity.BitacoraTicket;
import mx.desarrollo.entity.Usuario;

@Named("bitacoraUI")
@ViewScoped
public class BitacoraTicketsBeanUI implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private final DelegateTicket delegateTicket = new DelegateTicket();

    private List<BitacoraTicket> todosLosRegistros = new ArrayList<>();
    private List<BitacoraTicket> registros = new ArrayList<>();
    private List<BitacoraTicket> registrosFiltrados;

    private String prioridadFiltro;
    private LocalDate fechaFiltro;
    private String estadoFiltro;

    @PostConstruct
    public void init() {
        cargarBitacora();
    }

    public void cargarBitacora() {
        try {
            Usuario admin = obtenerUsuarioSesion();
            if (admin == null) {
                return;
            }
            todosLosRegistros = delegateTicket.obtenerBitacoraGeneral(admin.getRfc());
            aplicarFiltros();
        } catch (Exception e) {
            todosLosRegistros = new ArrayList<>();
            registros = new ArrayList<>();
            registrosFiltrados = null;
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Error al consultar bitácora", e.getMessage()));
        }
    }

    public void aplicarFiltros() {
        if (todosLosRegistros == null) {
            registros = new ArrayList<>();
            registrosFiltrados = null;
            return;
        }
        registros = todosLosRegistros.stream()
                .filter(b -> prioridadFiltro == null || prioridadFiltro.isBlank()
                        || (b.getTicket() != null && prioridadFiltro.equalsIgnoreCase(b.getTicket().getPrioridad())))
                .filter(b -> estadoFiltro == null || estadoFiltro.isBlank()
                        || (b.getTicket() != null && estadoFiltro.equalsIgnoreCase(b.getTicket().getEstado())))
                .filter(b -> fechaFiltro == null
                        || (b.getFechaHora() != null && b.getFechaHora().toLocalDate().equals(fechaFiltro)))
                .collect(Collectors.toList());
        registrosFiltrados = null;
    }

    public void limpiarFiltros() {
        this.prioridadFiltro = null;
        this.fechaFiltro = null;
        this.estadoFiltro = null;
        aplicarFiltros();
    }

    public String formatearFecha(LocalDateTime fecha) {
        return fecha != null ? fecha.format(FORMATO_FECHA) : "-";
    }

    public String etiquetarEvento(String tipoEvento) {
        if (tipoEvento == null) {
            return "-";
        }
        return switch (tipoEvento) {
            case "CREACION" -> "Creación de ticket";
            case "ESTADO" -> "Cambio de estado";
            case "ASIGNACION" -> "Cambio de asignación";
            case "PRIORIDAD" -> "Cambio de prioridad";
            default -> tipoEvento;
        };
    }

    private Usuario obtenerUsuarioSesion() {
        FacesContext context = FacesContext.getCurrentInstance();
        if (context == null) {
            return null;
        }
        return (Usuario) context.getExternalContext().getSessionMap().get("usuarioAutenticado");
    }

    public List<BitacoraTicket> getRegistros() {
        return registros;
    }

    public List<BitacoraTicket> getRegistrosFiltrados() {
        return registrosFiltrados;
    }

    public void setRegistrosFiltrados(List<BitacoraTicket> registrosFiltrados) {
        this.registrosFiltrados = registrosFiltrados;
    }

    public String getPrioridadFiltro() {
        return prioridadFiltro;
    }

    public void setPrioridadFiltro(String prioridadFiltro) {
        this.prioridadFiltro = prioridadFiltro;
    }

    public LocalDate getFechaFiltro() {
        return fechaFiltro;
    }

    public void setFechaFiltro(LocalDate fechaFiltro) {
        this.fechaFiltro = fechaFiltro;
    }

    public String getEstadoFiltro() {
        return estadoFiltro;
    }

    public void setEstadoFiltro(String estadoFiltro) {
        this.estadoFiltro = estadoFiltro;
    }
}