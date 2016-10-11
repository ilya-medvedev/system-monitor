package medvedev.ilya.monitor.web.config;

import akka.actor.TypedActorFactory;
import medvedev.ilya.monitor.sensor.Sensor;
import medvedev.ilya.monitor.web.handler.Handler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;

@Configuration
public class HandlerConfig {
    @Bean
    public WebSocketHandler webSocketHandler(
            final TypedActorFactory typedActorFactory,
            final Sensor[] sensors,
            @Value("${monitor.period}") final byte period
    ) {
        return new Handler(typedActorFactory, sensors, period);
    }
}
