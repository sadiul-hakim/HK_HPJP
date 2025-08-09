package xyz.sadiulhakim.example.actor;

import xyz.sadiulhakim.util.HibernateUtil;

import java.util.List;

public class ActorTest {
    public static void main(String[] args) {

        ActorRepoImplementation implementation = new ActorRepoImplementation(HibernateUtil.getSessionFactory());
        ActorService service = new ActorService(implementation);

        List<Actor> actors = service.findAll();
        System.out.println(actors);
    }
}
