package mx.desarrollo.delegate;

import mx.desarrollo.entity.Seguimiento;
import mx.desarrollo.entity.Ticket;
import mx.desarrollo.facade.FacadeSeguimiento;
import mx.desarrollo.integration.ServiceFacadeLocator;
import java.util.List;

public class DelegateSeguimiento {

    private final FacadeSeguimiento facadeSeguimiento = ServiceFacadeLocator.getInstanceFacadeSeguimiento();

    public void registrarObservacion(Ticket ticket, String descripcion) {
        facadeSeguimiento.registrarObservacion(ticket, descripcion);
    }

    public List<Seguimiento> consultarPorTicket(Integer idTicket) {
        return facadeSeguimiento.consultarPorTicket(idTicket);
    }
}