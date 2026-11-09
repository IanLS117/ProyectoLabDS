package mx.desarrollo.persistence.persistence;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class HibernateUtil {

    private static EntityManagerFactory factory;

    private static synchronized EntityManagerFactory getFactory() {
        if (factory == null || !factory.isOpen()) {
            factory = Persistence.createEntityManagerFactory("persistencePU", crearPropiedades(System.getenv()));
        }
        return factory;
    }

    static Map<String, Object> crearPropiedades(Map<String, String> entorno) {
        String usuario = entorno.get("SIGETI_DB_USER");
        if (usuario == null || usuario.isBlank()) {
            throw new IllegalStateException("Configura SIGETI_DB_USER.");
        }
        Map<String, Object> propiedades = new HashMap<>();
        propiedades.put("jakarta.persistence.jdbc.user", usuario);
        propiedades.put("jakarta.persistence.jdbc.password", entorno.getOrDefault("SIGETI_DB_PASSWORD", ""));
        String url = entorno.get("SIGETI_DB_URL");
        if (url != null && !url.isBlank()) {
            if (!url.startsWith("jdbc:mysql://")) {
                throw new IllegalArgumentException("SIGETI_DB_URL debe comenzar con jdbc:mysql://. Ejemplo: jdbc:mysql://127.0.0.1:3306/sigeti_sprint1");
            }
            propiedades.put("jakarta.persistence.jdbc.url", url);
        }
        String motor = entorno.get("SIGETI_DB_ENGINE");
        if (motor != null && !motor.isBlank()) {
            String dialecto = switch (motor.strip().toLowerCase(Locale.ROOT)) {
                case "mysql" -> "org.hibernate.dialect.MySQLDialect";
                case "mariadb" -> "org.hibernate.dialect.MariaDBDialect";
                default -> throw new IllegalArgumentException("SIGETI_DB_ENGINE debe ser MySQL o MariaDB.");
            };
            propiedades.put("hibernate.dialect", dialecto);
        }
        return propiedades;
    }

    public static EntityManager getEntityManager() {
        return getFactory().createEntityManager();
    }

    public static synchronized void close() {
        if (factory != null && factory.isOpen()) {
            factory.close();
        }
    }
}
