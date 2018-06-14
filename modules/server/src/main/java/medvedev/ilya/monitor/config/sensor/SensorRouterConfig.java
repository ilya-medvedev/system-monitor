package medvedev.ilya.monitor.config.sensor;

import medvedev.ilya.monitor.sensor.SensorMessage;
import medvedev.ilya.monitor.sensor.service.SensorService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Configuration
public class SensorRouterConfig {
    @Bean
    public RouterFunction<ServerResponse> sensorRouter(
            final SensorService sensorService,
            @Value("${monitor.period}") final byte period
    ) {
        final Duration duration = Duration.ofSeconds(period);

        final Flux<SensorMessage> sensorFlux = Flux.interval(duration)
//                .log()
                .map(i -> sensorService.currentValue())
                .doOnCancel(sensorService::cleanup)
                .share();

        final Mono<ServerResponse> sensorResponse = ServerResponse.ok()
                .contentType(MediaType.TEXT_EVENT_STREAM)
                .body(sensorFlux, SensorMessage.class)
                .cache();

        return RouterFunctions.route(RequestPredicates.GET("/sensors"), request -> sensorResponse);
    }
}
