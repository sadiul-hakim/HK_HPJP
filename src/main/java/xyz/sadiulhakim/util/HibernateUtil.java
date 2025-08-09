package xyz.sadiulhakim.util;

import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import xyz.sadiulhakim.event.Event;
import xyz.sadiulhakim.example.actor.Actor;

public class HibernateUtil {
    private HibernateUtil() {
    }

    //    private static final SessionFactory factory = buildSessionFactory();
    private static final SessionFactory factory = setUp();

    // The SessionFactory is a thread-safe object that’s instantiated once to serve the entire application.
    private static SessionFactory buildSessionFactory() {
        try {
            Configuration configuration = new Configuration();
            configuration.addAnnotatedClass(Actor.class);
            return configuration.buildSessionFactory();
        } catch (Throwable ex) {
            throw new ExceptionInInitializerError("Initial SessionFactory creation failed." + ex);
        }
    }

    private static SessionFactory setUp() {
        final StandardServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder().build();
        try {
            return new MetadataSources(serviceRegistry)
                    .addAnnotatedClasses(Event.class, Actor.class)
                    .buildMetadata()
                    .buildSessionFactory();
        } catch (Exception e) {
            StandardServiceRegistryBuilder.destroy(serviceRegistry);
            throw new ExceptionInInitializerError("Initial SessionFactory creation failed." + e);
        }
    }

    public static SessionFactory getSessionFactory() {
        return factory;
    }
}
