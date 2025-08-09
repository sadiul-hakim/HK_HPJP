package xyz.sadiulhakim.util;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import xyz.sadiulhakim.example.actor.Actor;

public class HibernateUtil {
    private HibernateUtil() {
    }

    private static final SessionFactory factory = buildSessionFactory();

    private static SessionFactory buildSessionFactory() {
        try {
            Configuration configuration = new Configuration();
            configuration.addAnnotatedClass(Actor.class);
            return configuration.buildSessionFactory();
        } catch (Throwable ex) {
            throw new ExceptionInInitializerError("Initial SessionFactory creation failed." + ex);
        }
    }

    public static SessionFactory getSessionFactory() {
        return factory;
    }
}
