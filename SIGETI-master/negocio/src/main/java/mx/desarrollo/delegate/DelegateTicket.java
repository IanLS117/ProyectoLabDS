package mx.desarrollo.delegate;

import java.util.List;
import java.util.Map;

import mx.desarrollo.entity.Ticket;
import mx.desarrollo.entity.TipoIncidencia;
import mx.desarrollo.entity.Usuario;
import mx.desarrollo.persistence.dao.TicketDAO;
import mx.desarrollo.persistence.dao.TipoIncidenciaDAO;
import mx.desarrollo.persistence.integration.ServiceLocator;

public class DelegateTicket {

    public List<TipoIncidencia> obtenerTipos() {
        return new TipoIncidenciaDAO().obtenerActivos();
    }

    public Ticket registrarTicket(String rfc, String numeroInventario, Integer idTipo, String descripcion) {
        if (rfc == null || rfc.isBlank()) {
            throw new IllegalArgumentException("Inicia sesión para registrar un ticket.");
        }
        if (numeroInventario == null || numeroInventario.isBlank() || numeroInventario.length() > 30) {
            throw new IllegalArgumentException("Selecciona un equipo asignado a tu cuenta.");
        }
        if (idTipo == null || idTipo <= 0) {
            throw new IllegalArgumentException("Selecciona un tipo de incidencia.");
        }
        if (descripcion == null || descripcion.isBlank()) {
            throw new IllegalArgumentException("La descripción del problema es obligatoria.");
        }
        String descripcionNormalizada = descripcion.strip();
        if (descripcionNormalizada.length() > 4000) {
            throw new IllegalArgumentException("La descripción admite hasta 4000 caracteres.");
        }
        return new TicketDAO().registrar(rfc, numeroInventario, idTipo, descripcionNormalizada);
    }

    public List<Ticket> obtenerTodosAdmin(String rfc) {
        if (rfc == null || rfc.isBlank()) {
            throw new IllegalArgumentException("No se pudo identificar al usuario autenticado.");
        }
        return new TicketDAO().obtenerTodosAdmin(rfc);
    }

    public Map<String, Long> obtenerCargaTrabajoSoportes() {
        TicketDAO ticketDAO = new TicketDAO();
        return ticketDAO.contarCargaActivaPorSoporte();
    }

    public Ticket reasignarTicket(Integer idTicket, String rfcNuevoSoporte, Usuario administrador) {
        if (idTicket == null || rfcNuevoSoporte == null || rfcNuevoSoporte.trim().isEmpty()) {
            throw new IllegalArgumentException("Debe seleccionar un ticket y un técnico de soporte válido.");
        }
        if (administrador == null || administrador.getRfc() == null) {
            throw new IllegalArgumentException("No se pudo identificar al administrador en sesión.");
        }

        TicketDAO ticketDAO = new TicketDAO();
        return ticketDAO.reasignarSoporte(administrador.getRfc(), idTicket, rfcNuevoSoporte);
    }
}
