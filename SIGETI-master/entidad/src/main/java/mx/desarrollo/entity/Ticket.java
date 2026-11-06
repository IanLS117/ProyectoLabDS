package mx.desarrollo.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "ticket")
public class Ticket implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_ticket")
    private int idTicket;

    @Column(name = "folio", nullable = false, unique = true, length = 20)
    private String folio;

    @ManyToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "id_equipo", nullable = false)
    private Equipo equipo;

    @ManyToOne
    @JoinColumn(name = "id_tipo_incidencia", nullable = false)
    private TipoIncidencia tipoIncidencia;

    @Column(name = "descripcion", nullable = false)
    private String descripcion;

    @Column(name = "estado", nullable = false, length = 20)
    private String estado;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

    public Ticket() {}

    public int getIdTicket() { return idTicket; }
    public void setIdTicket(int idTicket) { this.idTicket = idTicket; }
    public String getFolio() { return folio; }
    public void setFolio(String folio) { this.folio = folio; }
    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
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