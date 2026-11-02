package mx.desarrollo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.io.Serializable;

@Entity
@Table(name = "equipo")
public class Equipo implements Serializable {

    @Id
    @Column(name = "Numero_Inventario")
    private String idEquipo;

    @Column(name = "Marca")
    private String marca;

    @Column(name = "Modelo")
    private String modelo;

    @Column(name = "Area_Asignada")
    private String areaAsignada;

    @Column(name = "Estado_Hardware")
    private String estadoHardware;

    @Column(name = "RFC_Usuario_Asignado")
    private String idUsuarioAsignado;

    public Equipo() {}

    public String getIdEquipo() { return idEquipo; }
    public void setIdEquipo(String idEquipo) { this.idEquipo = idEquipo; }

    public String getMarca() { return marca; }
    public void setMarca(String marca) { this.marca = marca; }

    public String getNombre() { return marca; }
    public void setNombre(String nombre) { this.marca = nombre; }

    public String getModelo() { return modelo; }
    public void setModelo(String modelo) { this.modelo = modelo; }

    public String getAreaAsignada() { return areaAsignada; }
    public void setAreaAsignada(String areaAsignada) { this.areaAsignada = areaAsignada; }

    public String getEstadoHardware() { return estadoHardware; }
    public void setEstadoHardware(String estadoHardware) { this.estadoHardware = estadoHardware; }

    public String getIdUsuarioAsignado() { return idUsuarioAsignado; }
    public void setIdUsuarioAsignado(String idUsuarioAsignado) { this.idUsuarioAsignado = idUsuarioAsignado; }
}