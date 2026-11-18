package mx.desarrollo.facade;

import mx.desarrollo.entity.Seguimiento;
import mx.desarrollo.persistence.dao.AtencionTicketDAO;
import java.util.List;

public class FacadeSeguimiento {

    public void registrarObservacion(String rfc, Integer idTicket, String descripcion) {
        new AtencionTicketDAO().registrarObservacion(rfc, idTicket, descripcion);
    }

    public List<Seguimiento> consultarPorTicket(String rfc, Integer idTicket) {
        return new AtencionTicketDAO().consultarHistorial(rfc, idTicket);
    }
}
