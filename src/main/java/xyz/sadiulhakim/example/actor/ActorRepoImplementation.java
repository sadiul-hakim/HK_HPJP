package xyz.sadiulhakim.example.actor;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.List;
import java.util.Optional;

public class ActorRepoImplementation implements ActorRepo {

    private final SessionFactory sessionFactory;

    public ActorRepoImplementation(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public boolean save(Actor actor) {
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();
            session.persist(actor);
            tx.commit();
            return true;
        } catch (Exception ex) {
            if (tx != null && tx.isActive()) tx.rollback();
            ex.printStackTrace();
        }
        return false;
    }

    @Override
    public Optional<Actor> findById(long id) {
        try (Session session = sessionFactory.openSession()) {
            Actor actor = session.find(Actor.class, id);
            return Optional.ofNullable(actor);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public List<Actor> findAll() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("from Actor", Actor.class).list();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return List.of();
    }

    @Override
    public boolean deleteById(long id) {
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();

            Actor actor = session.find(Actor.class, id);
            if (actor != null) {
                session.remove(actor);
            }

            tx.commit();
            return true;
        } catch (Exception ex) {
            if (tx != null && tx.isActive()) tx.rollback();
            ex.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean update(Actor actor) {
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();

            Actor oldActor = session.find(Actor.class, actor.getActorId());
            if (oldActor != null) {
                oldActor.setFirstName(actor.getFirstName());
                oldActor.setLastName(actor.getLastName());
                oldActor.setLastUpdate(actor.getLastUpdate());
            }

            tx.commit();
            return true;
        } catch (Exception ex) {
            if (tx != null && tx.isActive()) tx.rollback();
            ex.printStackTrace();
        }
        return false;
    }
}