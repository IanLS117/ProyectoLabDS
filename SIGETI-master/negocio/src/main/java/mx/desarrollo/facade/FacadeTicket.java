package mx.desarrollo.facade;

import mx.desarrollo.delegate.DelegateTicket;
import mx.desarrollo.entity.Ticket;

public class FacadeTicket {

    private final DelegateTicket delegate = new DelegateTicket();

    public void registrarTicket(Ticket ticket) throws Exception {
        delegate.registrarTicket(ticket);
    }
}