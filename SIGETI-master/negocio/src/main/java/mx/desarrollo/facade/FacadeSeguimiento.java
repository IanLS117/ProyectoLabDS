package mx.desarrollo.facade;

import mx.desarrollo.entity.Seguimiento;
import mx.desarrollo.entity.SeguimientoId;
import mx.desarrollo.entity.Ticket;
import mx.desarrollo.persistence.dao.SeguimientoDAO;
import mx.desarrollo.persistence.integration.ServiceLocator;
import java.util.Date;
import java.util.List;

public class FacadeSeguimiento {

    private final SeguimientoDAO seguimientoDAO = ServiceLocator.getInstanceSeguimientoDAO();

    public void registrarObservacion(Ticket ticket, String descripcion) throws IllegalArgumentException {
        if (ticket == null || ticket.getIdTicket() == null) {
            throw new IllegalArgumentException("Debe seleccionar un ticket válido.");
        }
        if (descripcion == null || descripcion.trim().isEmpty()) {
            throw new IllegalArgumentException("La observación no puede estar vacía.");
        }
        if (!"En atención".equalsIgnoreCase(ticket.getEstado()) &&
                !"En Curso".equalsIgnoreCase(ticket.getEstado())) {
            throw new IllegalArgumentException("Solo se pueden registrar observaciones cuando el ticket está 'En atención'.");
        }

        int siguienteNumero = seguimientoDAO.obtenerSiguienteNumero(ticket.getIdTicket());

        Seguimiento seguimiento = new Seguimiento();
        seguimiento.setId(new SeguimientoId(ticket.getIdTicket(), siguienteNumero));
        seguimiento.setTicket(ticket);
        seguimiento.setFecha(new Date());
        seguimiento.setDescripcion(descripcion.trim());
        seguimiento.setEsSolucionFinal(false);

        seguimientoDAO.guardar(seguimiento); // Verifica si en AbstractDAO el método se llama save() o create()
    }

    public List<Seguimiento> consultarPorTicket(Integer idTicket) {
        if (idTicket == null) {
            return List.of();
        }
        return seguimientoDAO.listarPorTicket(idTicket);
    }
}