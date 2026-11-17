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
import mx.desarrollo.entity.Ticket;
import mx.desarrollo.facade.FacadeTicket;

@Named("misTicketsUI")
@ViewScoped
public class MisTicketsBeanUI implements Serializable {

    private List<Ticket> tickets = List.of();
    private boolean cargaCorrecta;

    public void cargar() {
        tickets = List.of();
        cargaCorrecta = false;
        try {
            tickets = new FacadeTicket().obtenerMisTickets(SesionActual.rfc());
            cargaCorrecta = true;
        } catch (IllegalArgumentException error) {
            mensaje(FacesMessage.SEVERITY_WARN, error.getMessage());
        } catch (RuntimeException error) {
            Logger.getLogger(MisTicketsBeanUI.class.getName())
                    .log(Level.SEVERE, "Error al consultar los tickets del usuario", error);
            mensaje(FacesMessage.SEVERITY_ERROR,
                    "No fue posible consultar tus tickets. Intenta más tarde.");
        }
    }

    private void mensaje(FacesMessage.Severity severidad, String texto) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severidad, texto, null));
    }

    public List<Ticket> getTickets() { return tickets; }
    public boolean isCargaCorrecta() { return cargaCorrecta; }
}
