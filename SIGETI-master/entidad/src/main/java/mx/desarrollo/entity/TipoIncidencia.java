package mx.desarrollo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "tipo_incidencia")
public class TipoIncidencia implements Serializable {

    @Id
    @Column(name = "id_tipo_incidencia")
    private Integer idTipoIncidencia;

    @Column(name = "nombre", nullable = false, length = 80)
    private String nombre;

    @Column(name = "activo", nullable = false)
    private boolean activo;

    public Integer getIdTipoIncidencia() { return idTipoIncidencia; }
    public String getNombre() { return nombre; }
    public boolean isActivo() { return activo; }
}
