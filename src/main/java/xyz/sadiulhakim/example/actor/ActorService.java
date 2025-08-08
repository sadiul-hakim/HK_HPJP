package xyz.sadiulhakim.example.actor;

import java.util.List;

public class ActorService {
    private final ActorRepo actorRepo;

    public ActorService(ActorRepo actorRepo) {
        this.actorRepo = actorRepo;
    }

    public List<Actor> findAll(){
        return actorRepo.findAll();
    }
}
