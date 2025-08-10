package xyz.sadiulhakim.example.actor;

import java.util.List;

public class ActorTest {
    public static void main(String[] args) {
        ActorRepoImplementation implementation = new ActorRepoImplementation();
        ActorService service = new ActorService(implementation);

        List<Actor> actors = service.findAll();
        System.out.println(actors);
    }
}
