package mx.desarrollo.entity;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "usuario")
public class Usuario implements Serializable {

    @Id
    @Column(name = "RFC", length = 13, nullable = false)
    private String rfc;

    @Column(name = "Contrasena", nullable = false)
    private String contrasena;

    @Column(name = "Nombre")
    private String nombre;

    public Usuario() {}

    public String getRfc() { return rfc; }
    public void setRfc(String rfc) { this.rfc = rfc; }

    public String getContrasena() { return contrasena; }
    public void setContrasena(String contrasena) { this.contrasena = contrasena; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
}