package mx.desarrollo.persistence.persistence;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.util.HashMap;
import java.util.Map;

/**
 * Clase utilitaria para inicializar y obtener el {@link EntityManagerFactory}.
 */
public class HibernateUtil {
    //Issac Inzunza
    private static final EntityManagerFactory ENTITY_MANAGER_FACTORY = buildEntityManagerFactory();

    /**
     * Crea la instancia de EntityManagerFactory a partir del archivo persistence.xml.
     */
    private static EntityManagerFactory buildEntityManagerFactory() {
        Map<String, Object> propiedades = new HashMap<>();

        String usuario = System.getenv("SIGETI_DB_USER");
        String contrasena = System.getenv("SIGETI_DB_PASSWORD");

        if (usuario == null || contrasena == null) {
            throw new IllegalStateException(
                    "Configura SIGETI_DB_USER y SIGETI_DB_PASSWORD."
            );
        }

        propiedades.put("jakarta.persistence.jdbc.user", usuario);
        propiedades.put("jakarta.persistence.jdbc.password", contrasena);

        return Persistence.createEntityManagerFactory(
                "persistencePU",
                propiedades
        );
    }

    public static EntityManagerFactory getEntityManagerFactory() {
        return ENTITY_MANAGER_FACTORY;
    }

    public static EntityManager getEntityManager() {
        return ENTITY_MANAGER_FACTORY.createEntityManager();
    }

    public static void close() {
        if (ENTITY_MANAGER_FACTORY != null && ENTITY_MANAGER_FACTORY.isOpen()) {
            ENTITY_MANAGER_FACTORY.close();
        }
    }
}
