package mx.desarrollo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "usuario_final")
public class UsuarioFinal implements Serializable {

    @Id
    @Column(name = "rfc_usuario", length = 13)
    private String rfcUsuario;

    public String getRfcUsuario() {
        return rfcUsuario;
    }
}
