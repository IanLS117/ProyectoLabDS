package mx.desarrollo.facade;

import java.util.List;
import mx.desarrollo.delegate.DelegateEquipo;
import mx.desarrollo.entity.Equipo;

public class FacadeEquipo {

    public List<Equipo> obtenerEquipos(String rfc) {
        return new DelegateEquipo().obtenerEquipos(rfc);
    }
}
