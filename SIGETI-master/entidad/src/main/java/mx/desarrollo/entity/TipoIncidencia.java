package mx.desarrollo.entity;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "tipo_incidencia")
public class TipoIncidencia implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tipo_incidencia")
    private int idTipoIncidencia;

    @Column(name = "nombre", nullable = false, length = 50)
    private String nombre;

    @Column(name = "descripcion", length = 255)
    private String descripcion;

    public TipoIncidencia() {}

    public int getIdTipoIncidencia() { return idTipoIncidencia; }
    public void setIdTipoIncidencia(int idTipoIncidencia) { this.idTipoIncidencia = idTipoIncidencia; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
}