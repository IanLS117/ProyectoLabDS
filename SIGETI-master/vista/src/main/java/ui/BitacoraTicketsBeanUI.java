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
import mx.desarrollo.entity.BitacoraTicket;
import mx.desarrollo.entity.Ticket;
import mx.desarrollo.entity.Usuario;

@Named("bitacoraUI")
@ViewScoped
public class BitacoraTicketsBeanUI implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private final DelegateTicket delegateTicket = new DelegateTicket();

    private List<BitacoraTicket> registros = new ArrayList<>();
    private List<BitacoraTicket> registrosFiltrados;
    private List<Ticket> listaTickets = new ArrayList<>();
    private Integer idTicketFiltro;

    @PostConstruct
    public void init() {
        cargarTicketsSelector();
        cargarBitacora();
    }

    public void cargarTicketsSelector() {
        try {
            Usuario admin = obtenerUsuarioSesion();
            if (admin != null) {
                this.listaTickets = delegateTicket.obtenerTodosAdmin(admin.getRfc());
            }
        } catch (Exception e) {
            this.listaTickets = new ArrayList<>();
        }
    }

    public void cargarBitacora() {
        try {
            Usuario admin = obtenerUsuarioSesion();
            if (admin == null) {
                return;
            }
            if (idTicketFiltro != null && idTicketFiltro > 0) {
                registros = delegateTicket.obtenerBitacoraTicket(admin.getRfc(), idTicketFiltro);
            } else {
                registros = delegateTicket.obtenerBitacoraGeneral(admin.getRfc());
            }
            registrosFiltrados = null;
        } catch (Exception e) {
            registros = new ArrayList<>();
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Error al consultar bitácora", e.getMessage()));
        }
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

    public List<Ticket> getListaTickets() {
        return listaTickets;
    }

    public Integer getIdTicketFiltro() {
        return idTicketFiltro;
    }

    public void setIdTicketFiltro(Integer idTicketFiltro) {
        this.idTicketFiltro = idTicketFiltro;
    }
}
