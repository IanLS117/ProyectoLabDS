package mx.desarrollo.integration;

import mx.desarrollo.facade.FacadeTicket;
import mx.desarrollo.facade.FacadeUsuario;

public class ServiceFacadeLocator {

    private static FacadeTicket facadeTicket;
    private static FacadeUsuario facadeUsuario;

    public static FacadeTicket getInstanceFacadeTicket() {
        if (facadeTicket == null) {
            facadeTicket = new FacadeTicket();
        }
        return facadeTicket;
    }

    public static FacadeUsuario getInstanceFacadeUsuario() {
        if (facadeUsuario == null) {
            facadeUsuario = new FacadeUsuario();
        }
        return facadeUsuario;
    }
}