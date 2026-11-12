package mx.desarrollo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class SeguimientoId implements Serializable {

    @Column(name = "ID_Ticket", nullable = false)
    private Integer idTicket;

    @Column(name = "Numero_Seguimiento", nullable = false)
    private Integer numeroSeguimiento;

    public SeguimientoId() {
    }

    public SeguimientoId(Integer idTicket, Integer numeroSeguimiento) {
        this.idTicket = idTicket;
        this.numeroSeguimiento = numeroSeguimiento;
    }

    public Integer getIdTicket() {
        return idTicket;
    }

    public void setIdTicket(Integer idTicket) {
        this.idTicket = idTicket;
    }

    public Integer getNumeroSeguimiento() {
        return numeroSeguimiento;
    }

    public void setNumeroSeguimiento(Integer numeroSeguimiento) {
        this.numeroSeguimiento = numeroSeguimiento;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SeguimientoId)) return false;
        SeguimientoId that = (SeguimientoId) o;
        return Objects.equals(idTicket, that.idTicket) &&
                Objects.equals(numeroSeguimiento, that.numeroSeguimiento);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idTicket, numeroSeguimiento);
    }
}