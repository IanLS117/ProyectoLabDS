package ui;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import mx.desarrollo.entity.Equipo;
import mx.desarrollo.persistence.dao.EquipoDAO;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Named("indexUI")
@ViewScoped
public class IndexUI implements Serializable {

    private String vistaActual;
    private List<Equipo> equiposAsignados;
    private EquipoDAO equipoDAO;

    @Inject
    private LoginBeanUI loginUI;

    @PostConstruct
    public void init() {
        this.vistaActual = "inicio";
        this.equipoDAO = new EquipoDAO();
        this.equiposAsignados = new ArrayList<>();
    }

    public void irAInicio() {
        this.vistaActual = "inicio";
    }

    public void irAConsultas() {
        this.vistaActual = "consultas";
        if (loginUI != null && loginUI.getUsuario() != null && loginUI.getUsuario().getRfc() != null) {
            String rfcUsuario = loginUI.getUsuario().getRfc();
            this.equiposAsignados = equipoDAO.obtenerEquiposPorUsuario(rfcUsuario);
        }
    }

    // --- GETTERS & SETTERS ---
    public String getVistaActual() { return vistaActual; }
    public void setVistaActual(String vistaActual) { this.vistaActual = vistaActual; }

    public List<Equipo> getEquiposAsignados() { return equiposAsignados; }
    public void setEquiposAsignados(List<Equipo> equiposAsignados) { this.equiposAsignados = equiposAsignados; }

    public LoginBeanUI getLoginUI() { return loginUI; }
    public void setLoginUI(LoginBeanUI loginUI) { this.loginUI = loginUI; }
}