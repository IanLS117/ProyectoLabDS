package mx.desarrollo.facade;

import java.util.List;
import mx.desarrollo.delegate.DelegateTicket;
import mx.desarrollo.entity.Ticket;
import mx.desarrollo.entity.TipoIncidencia;

public class FacadeTicket {

    public List<TipoIncidencia> obtenerTipos() {
        return new DelegateTicket().obtenerTipos();
    }

    public Ticket registrarTicket(String rfc, String numeroInventario, Integer idTipo, String descripcion) {
        return new DelegateTicket().registrarTicket(rfc, numeroInventario, idTipo, descripcion);
    }
}
