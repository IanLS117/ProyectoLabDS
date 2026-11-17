package mx.desarrollo.facade;

import java.util.List;
import java.util.Map;

import mx.desarrollo.delegate.DelegateTicket;
import mx.desarrollo.entity.Ticket;
import mx.desarrollo.entity.TipoIncidencia;
import mx.desarrollo.entity.Usuario;

public class FacadeTicket {

    public List<TipoIncidencia> obtenerTipos() {
        return new DelegateTicket().obtenerTipos();
    }

    public Ticket registrarTicket(String rfc, String numeroInventario, Integer idTipo, String descripcion) {
        return new DelegateTicket().registrarTicket(rfc, numeroInventario, idTipo, descripcion);
    }

    public Map<String, Long> obtenerCargaTrabajoSoportes() {
        return new DelegateTicket().obtenerCargaTrabajoSoportes();
    }

    public Ticket reasignarTicket(Integer idTicket, String rfcNuevoSoporte, Usuario administrador) {
        return new DelegateTicket().reasignarTicket(idTicket, rfcNuevoSoporte, administrador);
    }

    public List<Ticket> obtenerTodosAdmin(String rfc) {
        return new DelegateTicket().obtenerTodosAdmin(rfc);
    }
}
