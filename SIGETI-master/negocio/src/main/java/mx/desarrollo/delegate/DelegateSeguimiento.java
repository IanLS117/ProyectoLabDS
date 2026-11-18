package mx.desarrollo.delegate;

import mx.desarrollo.entity.Seguimiento;
import mx.desarrollo.facade.FacadeSeguimiento;
import mx.desarrollo.integration.ServiceFacadeLocator;
import java.util.List;

public class DelegateSeguimiento {

    private final FacadeSeguimiento facadeSeguimiento = ServiceFacadeLocator.getInstanceFacadeSeguimiento();

    public void registrarObservacion(String rfc, Integer idTicket, String descripcion) {
        facadeSeguimiento.registrarObservacion(rfc, idTicket, descripcion);
    }

    public List<Seguimiento> consultarPorTicket(String rfc, Integer idTicket) {
        return facadeSeguimiento.consultarPorTicket(rfc, idTicket);
    }
}
