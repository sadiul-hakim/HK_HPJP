package xyz.sadiulhakim.event;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import xyz.sadiulhakim.util.JpaUtil;

import java.util.function.Consumer;

public class EventService {

    public static void main(String[] args) {
        EntityManager entityManager = JpaUtil.getEntityManager();
        AuditReader reader = AuditReaderFactory.get(entityManager);
        Event event = reader.find(Event.class, 3, 1);
        System.out.println(event);
//
//        inTransaction(entityManager -> {
//            entityManager.persist(new Event("Learning Audit Event", LocalDateTime.now()));
//            TypedQuery<Event> query = entityManager.createQuery("from Event", Event.class);
//            query.getResultList().forEach(event -> System.out.println(event.getTitle()));
//        });
    }

    static void inTransaction(Consumer<EntityManager> work) {
        EntityManager entityManager = JpaUtil.getEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            work.accept(entityManager);
            transaction.commit();
        } catch (Exception ex) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            throw ex;
        } finally {
            entityManager.close();
        }
    }
}
