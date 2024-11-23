package com.unir.config;

import com.unir.model.mysql.*;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.service.ServiceRegistry;
import java.util.Properties;

@Slf4j
public class HibernateMySqlConfig {

    // Factoría de sesiones de Hibernate
    private static SessionFactory sessionFactory;

    // Nombre de la base de datos
    private static final String DATABASE = "employees";

    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            try {
                Configuration configuration = getConfiguration();

                // Clases anotadas - Aqui indicamos las clases (entidades) que queremos que Hibernate gestione.
                configuration.addAnnotatedClass(Employee.class);
                configuration.addAnnotatedClass(Department.class);
                configuration.addAnnotatedClass(DeptEmployee.class);
                configuration.addAnnotatedClass(DeptManager.class);

                ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                        .applySettings(configuration.getProperties()).build();
                sessionFactory = configuration.buildSessionFactory(serviceRegistry);
            } catch (Exception e) {
                log.error("Error al crear la factoría de sesiones de MySQL: {}", e.getMessage(), e);
            }
        }
        return sessionFactory;
    }

    /**
     * Configuración de Hibernate equivalente al hibernate.cfg.xml.
     * Indicamos la base de datos, usuario y contraseña.
     * @return Configuracion basica de Hibernate para MySQL.
     */
    private static Configuration getConfiguration() {

        Configuration configuration = new Configuration();
        // Configuración de Hibernate equivalente al hibernate.cfg.xml
        Properties settings = new Properties();
        //settings.put(Environment.DRIVER, "com.mysql.cj.jdbc.Driver");
        settings.put(Environment.URL, "jdbc:mysql://" + System.getenv("MYSQL_HOST") + ":" + System.getenv("MYSQL_PORT") + "/" + DATABASE);
        settings.put(Environment.USER, System.getenv("MYSQL_USER"));
        settings.put(Environment.PASS, System.getenv("MYSQL_PASSWORD"));
        settings.put(Environment.DIALECT, "org.hibernate.dialect.MySQL8Dialect");
        settings.put(Environment.SHOW_SQL, "true");
        settings.put(Environment.CURRENT_SESSION_CONTEXT_CLASS, "thread");
        settings.put(Environment.HBM2DDL_AUTO, "validate");
        // Aplicar configuraciones
        configuration.setProperties(settings);
        return configuration;
    }
}
