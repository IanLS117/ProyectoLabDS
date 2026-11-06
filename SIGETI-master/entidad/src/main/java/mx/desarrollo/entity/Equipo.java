package mx.desarrollo.entity;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "equipo")
public class Equipo implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_equipo")
    private int idEquipo;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "numero_serie", nullable = false, length = 50)
    private String numeroSerie;

    @ManyToOne
    @JoinColumn(name = "id_usuario_resguardo", nullable = false)
    private Usuario usuarioResguardo;

    public Equipo() {}

    public int getIdEquipo() { return idEquipo; }
    public void setIdEquipo(int idEquipo) { this.idEquipo = idEquipo; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getNumeroSerie() { return numeroSerie; }
    public void setNumeroSerie(String numeroSerie) { this.numeroSerie = numeroSerie; }

    public Usuario getUsuarioResguardo() { return usuarioResguardo; }
    public void setUsuarioResguardo(Usuario usuarioResguardo) { this.usuarioResguardo = usuarioResguardo; }
}