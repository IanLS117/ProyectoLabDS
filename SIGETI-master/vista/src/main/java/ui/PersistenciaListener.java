package ui;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import mx.desarrollo.persistence.persistence.HibernateUtil;

@WebListener
public class PersistenciaListener implements ServletContextListener {

    @Override
    public void contextDestroyed(ServletContextEvent evento) {
        HibernateUtil.close();
    }
}
