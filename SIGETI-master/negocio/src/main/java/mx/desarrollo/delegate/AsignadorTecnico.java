package mx.desarrollo.delegate;

import java.util.Map;
import mx.desarrollo.entity.Usuario;

/** Aqui es para asignar al tecnico con menores tickets */
public class AsignadorTecnico {

    public Usuario seleccionar(Map<Usuario, Long> cargas) {
        Usuario tecnicoSeleccionado = null;
        long menorCarga = Long.MAX_VALUE;

        for (Usuario tecnico : cargas.keySet()) {
            long cargaActual = cargas.get(tecnico);

            if (tecnicoSeleccionado == null || cargaActual < menorCarga) {
                tecnicoSeleccionado = tecnico;
                menorCarga = cargaActual;
            } else if (cargaActual == menorCarga) {
                // Si tienen la misma carga, elegimos el RFC que va primero.
                if (tecnico.getRfc().compareTo(tecnicoSeleccionado.getRfc()) < 0) {
                    tecnicoSeleccionado = tecnico;
                }
            }
        }

        if (tecnicoSeleccionado == null) {
            throw new IllegalArgumentException("No hay tecnicos disponibles para asignar el ticket.");
        }

        return tecnicoSeleccionado;
    }
}
