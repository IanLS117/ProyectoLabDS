package mx.desarrollo.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "SEGUIMIENTO")
public class Seguimiento implements Serializable {

    @EmbeddedId
    private SeguimientoId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_ticket", insertable = false, updatable = false)
    private Ticket ticket;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "Fecha", nullable = false)
    private Date fecha;

    @Column(name = "Descripcion", nullable = false, columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "Es_Solucion_Final", nullable = false)
    private Boolean esSolucionFinal = false;

    public Seguimiento() {
    }

    public SeguimientoId getId() {
        return id;
    }

    public void setId(SeguimientoId id) {
        this.id = id;
    }

    public Ticket getTicket() {
        return ticket;
    }

    public void setTicket(Ticket ticket) {
        this.ticket = ticket;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Boolean getEsSolucionFinal() {
        return esSolucionFinal;
    }

    public void setEsSolucionFinal(Boolean esSolucionFinal) {
        this.esSolucionFinal = esSolucionFinal;
    }
}