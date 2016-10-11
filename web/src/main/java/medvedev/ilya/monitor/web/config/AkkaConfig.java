package medvedev.ilya.monitor.web.config;

import akka.actor.ActorSystem;
import akka.actor.TypedActor;
import akka.actor.TypedActorFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AkkaConfig {
    @Bean
    public ActorSystem actorSystem() {
        return ActorSystem.create();
    }

    @Bean
    public TypedActorFactory typedActor(final ActorSystem actorSystem) {
        return TypedActor.get(actorSystem);
    }
}
