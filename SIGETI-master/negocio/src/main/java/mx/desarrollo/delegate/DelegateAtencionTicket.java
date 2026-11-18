package mx.desarrollo.delegate;

import java.util.List;
import mx.desarrollo.entity.Ticket;
import mx.desarrollo.persistence.dao.AtencionTicketDAO;

public class DelegateAtencionTicket {

    public List<Ticket> listarAsignados(String rfc) {
        return new AtencionTicketDAO().listarAsignados(rfc);
    }

    public Ticket consultar(String rfc, Integer idTicket) {
        return new AtencionTicketDAO().consultar(rfc, idTicket);
    }

    public Ticket cambiarEstado(String rfc, Integer idTicket, String estadoEsperado,
            String nuevoEstado, String solucion) {
        return new AtencionTicketDAO().cambiarEstado(rfc, idTicket, estadoEsperado, nuevoEstado, solucion);
    }
}
