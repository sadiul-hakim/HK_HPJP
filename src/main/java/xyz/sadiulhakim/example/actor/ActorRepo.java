package xyz.sadiulhakim.example.actor;

import java.util.List;
import java.util.Optional;

public interface ActorRepo {
    boolean save(Actor actor);

    Optional<Actor> findById(long id);

    List<Actor> findAll();

    boolean deleteById(long id);

    boolean update(Actor actor);
}
