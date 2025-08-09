package xyz.sadiulhakim;

import org.hibernate.SessionFactory;
import xyz.sadiulhakim.event.Event;
import xyz.sadiulhakim.util.HibernateUtil;

public class Main {
    public static void main(String[] args) {
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        sessionFactory.inTransaction(session -> {
//            session.persist(new Event("Hibernate Learning Event", LocalDateTime.now()));
//            session.persist(new Actor("Musharraf", "Karim", LocalDateTime.now()));
            session.createSelectionQuery("from Event", Event.class).list()
                    .forEach(event -> System.out.println(event.getTitle()));
        });
    }
}

