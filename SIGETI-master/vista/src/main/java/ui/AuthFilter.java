package ui;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import mx.desarrollo.entity.Usuario;

public class AuthFilter implements Filter {

    @Override
    public void doFilter(
            ServletRequest request,
            ServletResponse response,
            FilterChain chain
    ) throws IOException, ServletException {

        HttpServletRequest solicitud =
                (HttpServletRequest) request;

        HttpServletResponse respuesta =
                (HttpServletResponse) response;

        String ruta = solicitud.getServletPath();

        respuesta.setHeader("Cache-Control", "no-store");

        boolean recursoPublico =
                ruta.equals("/login.xhtml")
                        || ruta.startsWith("/jakarta.faces.resource/")
                        || ruta.startsWith("/resources/");

        if (recursoPublico) {
            chain.doFilter(request, response);
            return;
        }

        HttpSession sesion = solicitud.getSession(false);
        Usuario usuario = null;

        if (sesion != null) {
            usuario = (Usuario) sesion
                    .getAttribute("usuarioAutenticado");
        }

        if (usuario == null) {
            respuesta.sendRedirect(
                    solicitud.getContextPath() + "/login.xhtml"
            );
            return;
        }

        if ("/seguimientos.xhtml".equals(ruta)
                && !usuario.tienePermiso("atender_tickets")) {
            respuesta.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        if ("/mis-equipos.xhtml".equals(ruta)
                && !usuario.tienePermiso("consultar_equipos")) {
            respuesta.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        if ("/nuevo-ticket.xhtml".equals(ruta)
                && (!usuario.tienePermiso("crear_ticket")
                || !usuario.tienePermiso("consultar_equipos"))) {
            respuesta.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        chain.doFilter(request, response);
    }
}
