package mx.desarrollo.facade;

import java.util.List;
import mx.desarrollo.delegate.DelegateAtencionTicket;
import mx.desarrollo.entity.Ticket;

public class FacadeAtencionTicket {

    public List<Ticket> listarAsignados(String rfc) {
        return new DelegateAtencionTicket().listarAsignados(rfc);
    }

    public Ticket consultar(String rfc, Integer idTicket) {
        return new DelegateAtencionTicket().consultar(rfc, idTicket);
    }

    public Ticket cambiarEstado(String rfc, Integer idTicket, String estadoEsperado,
            String nuevoEstado, String solucion) {
        return new DelegateAtencionTicket().cambiarEstado(rfc, idTicket, estadoEsperado, nuevoEstado, solucion);
    }
}
