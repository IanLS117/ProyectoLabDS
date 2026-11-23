package mx.desarrollo.integration;

import mx.desarrollo.facade.FacadeUsuario;
import mx.desarrollo.facade.FacadeSeguimiento;

public class ServiceFacadeLocator {

    private static FacadeUsuario facadeUsuario;

    public static FacadeUsuario getInstanceFacadeUsuario() {
        if (facadeUsuario == null) {
            facadeUsuario = new FacadeUsuario();
        }
        return facadeUsuario;
    }

    private static FacadeSeguimiento facadeSeguimiento;

    public static FacadeSeguimiento getInstanceFacadeSeguimiento() {
        if (facadeSeguimiento == null) {
            facadeSeguimiento = new FacadeSeguimiento();
        }
        return facadeSeguimiento;
    }

}
