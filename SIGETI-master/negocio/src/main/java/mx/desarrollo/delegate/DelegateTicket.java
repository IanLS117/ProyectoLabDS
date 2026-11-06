package mx.desarrollo.delegate;

import mx.desarrollo.entity.Ticket;
import mx.desarrollo.persistence.dao.TicketDAO;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DelegateTicket {

    private final TicketDAO ticketDAO = new TicketDAO();

    public void registrarTicket(Ticket ticket) throws Exception {
        if (ticket.getDescripcion() == null || ticket.getDescripcion().trim().isEmpty()) {
            throw new Exception("La descripción de la incidencia es obligatoria.");
        }
        if (ticket.getEquipo() == null) {
            throw new Exception("Debe seleccionar un equipo en resguardo.");
        }
        if (ticket.getTipoIncidencia() == null) {
            throw new Exception("Debe seleccionar un tipo de incidencia.");
        }

        // Generación automática de Folio único y datos por defecto
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));
        ticket.setFolio("TCK-" + timestamp);
        ticket.setEstado("Enviado");
        ticket.setFechaCreacion(LocalDateTime.now());

        ticketDAO.guardar(ticket);
    }
}