package mx.desarrollo.delegate;

import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class PasswordHash {

    public static boolean verificar(String contrasena, String hashGuardado) {
        if (contrasena == null || hashGuardado == null) {
            return false;
        }

        PBEKeySpec especificacion = null;

        try {
            String[] partes = hashGuardado.split("\\$");

            if (partes.length != 4 || !partes[0].equals("pbkdf2")) {
                return false;
            }

            int repeticiones = Integer.parseInt(partes[1]);

            if (repeticiones < 100000 || repeticiones > 1000000) {
                return false;
            }

            byte[] sal = Base64.getDecoder().decode(partes[2]);
            byte[] hashEsperado = Base64.getDecoder().decode(partes[3]);

            if (sal.length != 16 || hashEsperado.length != 32) {
                return false;
            }

            especificacion = new PBEKeySpec(
                    contrasena.toCharArray(),
                    sal,
                    repeticiones,
                    256
            );

            SecretKeyFactory fabrica =
                    SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");

            byte[] hashCalculado =
                    fabrica.generateSecret(especificacion).getEncoded();

            return MessageDigest.isEqual(hashEsperado, hashCalculado);

        } catch (GeneralSecurityException | IllegalArgumentException e) {
            return false;

        } finally {
            if (especificacion != null) {
                especificacion.clearPassword();
            }
        }
    }

     // aqui se utiliza el algoritmo PBKDF2 para verificar las contraseñas
    // el sal es un valor aleatorio guardado con el hash y el metodo calcula el resultado con la contrase;a ingresada u lo compara.
}
