/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.desarrollo.persistence.integration;

import mx.desarrollo.persistence.dao.TicketDAO;
import mx.desarrollo.persistence.dao.UsuarioDAO;

public class ServiceLocator {

    private static TicketDAO ticketDAO;
    private static UsuarioDAO usuarioDAO;

    public static TicketDAO getInstanceTicketDAO() {
        if (ticketDAO == null) {
            ticketDAO = new TicketDAO();
        }
        return ticketDAO;
    }

    public static UsuarioDAO getInstanceUsuarioDAO() {
        if (usuarioDAO == null) {
            usuarioDAO = new UsuarioDAO();
        }
        return usuarioDAO;
    }
}