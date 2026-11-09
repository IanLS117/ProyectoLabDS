package mx.desarrollo.delegate;

import java.util.List;
import mx.desarrollo.entity.Ticket;
import mx.desarrollo.entity.TipoIncidencia;
import mx.desarrollo.persistence.dao.TicketDAO;
import mx.desarrollo.persistence.dao.TipoIncidenciaDAO;

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
}
