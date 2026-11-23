package mx.desarrollo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "ticket")
public class Ticket implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_ticket")
    private Integer idTicket;

    @Column(name = "folio", nullable = false, unique = true, length = 40)
    private String folio;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "rfc_usuario_final", nullable = false)
    private UsuarioFinal usuario;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "numero_inventario", nullable = false)
    private Equipo equipo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_tipo_incidencia", nullable = false)
    private TipoIncidencia tipoIncidencia;

    @Column(name = "descripcion_problema", nullable = false, length = 4000)
    private String descripcion;

    @Column(name = "estatus", nullable = false, length = 20)
    private String estado;

    @Column(name = "fecha_hora_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

    public Integer getIdTicket() { return idTicket; }
    public String getFolio() { return folio; }
    public void setFolio(String folio) { this.folio = folio; }
    public UsuarioFinal getUsuario() { return usuario; }
    public void setUsuario(UsuarioFinal usuario) { this.usuario = usuario; }
    public Equipo getEquipo() { return equipo; }
    public void setEquipo(Equipo equipo) { this.equipo = equipo; }
    public TipoIncidencia getTipoIncidencia() { return tipoIncidencia; }
    public void setTipoIncidencia(TipoIncidencia tipoIncidencia) { this.tipoIncidencia = tipoIncidencia; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
}
