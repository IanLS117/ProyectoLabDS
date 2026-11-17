package mx.desarrollo.delegate;

import java.util.LinkedHashMap;
import java.util.Map;
import mx.desarrollo.entity.Usuario;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AsignadorTecnicoTest {
    private final AsignadorTecnico asignador = new AsignadorTecnico();

    private Usuario tecnico(String rfc) {
        Usuario usuario = new Usuario();
        usuario.setRfc(rfc);
        return usuario;
    }

    @Test
    void eligeMenorCargaAunqueNoSeaElPrimerRfc() {
        Usuario a = tecnico("AAA"), b = tecnico("BBB"), c = tecnico("CCC");
        assertSame(b, asignador.seleccionar(Map.of(a, 7L, b, 1L, c, 3L)));
    }

    @Test
    void incluyeTecnicosSinTickets() {
        Usuario a = tecnico("AAA"), b = tecnico("BBB");
        assertSame(b, asignador.seleccionar(Map.of(a, 2L, b, 0L)));
    }

    @Test
    void desempataPorRfcSinDependerDelOrden() {
        Usuario a = tecnico("AAA"), b = tecnico("BBB");
        Map<Usuario, Long> cargas = new LinkedHashMap<>();
        cargas.put(b, 2L);
        cargas.put(a, 2L);
        assertSame(a, asignador.seleccionar(cargas));
    }

    @Test
    void asignaAlUnicoTecnico() {
        Usuario unico = tecnico("SOPORTE");
        assertSame(unico, asignador.seleccionar(Map.of(unico, 20L)));
    }

    @Test
    void rechazaRegistroSinTecnicos() {
        assertThrows(IllegalArgumentException.class, () -> asignador.seleccionar(Map.of()));
    }
}
