package xyz.sadiulhakim.example.actor;

import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;
import xyz.sadiulhakim.util.JpaUtil;

import java.util.List;
import java.util.Optional;

public class ActorRepoImplementation implements ActorRepo {

    @Override
    public boolean save(Actor actor) {

        try (var entityManager = JpaUtil.getEntityManager()) {

            EntityTransaction transaction = entityManager.getTransaction();
            try {

                transaction.begin();
                entityManager.persist(actor);
                transaction.commit();
                return true;
            } catch (Exception ex) {
                if (transaction != null && transaction.isActive()) {
                    transaction.rollback();
                }

                ex.printStackTrace();
            }
        }

        return false;
    }

    @Override
    public Optional<Actor> findById(long id) {
        try (var entityManager = JpaUtil.getEntityManager()) {
            Actor actor = entityManager.find(Actor.class, id);
            return Optional.of(actor);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return Optional.empty();
    }

    @Override
    public List<Actor> findAll() {
        try (var entityManager = JpaUtil.getEntityManager()) {
            TypedQuery<Actor> query = entityManager.createQuery("select a from Actor a", Actor.class);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return List.of();
    }

    @Override
    public boolean deleteById(long id) {
        try (var entityManager = JpaUtil.getEntityManager()) {

            EntityTransaction transaction = entityManager.getTransaction();
            try {

                transaction.begin();
                Actor actor = entityManager.find(Actor.class, id);
                if (actor != null) {
                    entityManager.remove(actor);
                }

                transaction.commit();
                return true;
            } catch (Exception ex) {
                if (transaction != null && transaction.isActive()) {
                    transaction.rollback();
                }

                ex.printStackTrace();
            }
        }

        return false;
    }

    @Override
    public boolean update(Actor actor) {
        try (var entityManager = JpaUtil.getEntityManager()) {

            EntityTransaction transaction = entityManager.getTransaction();
            try {

                transaction.begin();
                Actor oldActor = entityManager.find(Actor.class, actor.getActorId());
                if (oldActor != null) {
                    oldActor.setFirstName(actor.getFirstName());
                    oldActor.setLastName(actor.getLastName());
                    oldActor.setLastUpdate(actor.getLastUpdate());
                }

                transaction.commit();
                return true;
            } catch (Exception ex) {
                if (transaction != null && transaction.isActive()) {
                    transaction.rollback();
                }

                ex.printStackTrace();
            }
        }

        return false;
    }
}
