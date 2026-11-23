package mx.desarrollo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "equipo")
public class Equipo implements Serializable {

    @Id
    @Column(name = "numero_inventario", length = 30)
    private String numeroInventario;

    @Column(name = "marca", nullable = false, length = 80)
    private String marca;

    @Column(name = "modelo", nullable = false, length = 80)
    private String modelo;

    @Column(name = "area_asignada", nullable = false, length = 80)
    private String areaAsignada;

    @Column(name = "estado_hardware", nullable = false, length = 30)
    private String estadoHardware;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rfc_usuario_asignado")
    private UsuarioFinal usuarioAsignado;

    public String getNumeroInventario() { return numeroInventario; }
    public String getMarca() { return marca; }
    public String getModelo() { return modelo; }
    public String getAreaAsignada() { return areaAsignada; }
    public String getEstadoHardware() { return estadoHardware; }
    public UsuarioFinal getUsuarioAsignado() { return usuarioAsignado; }
}
