package medvedev.ilya.monitor.handler;

import medvedev.ilya.monitor.sensor.SensorMessage;
import medvedev.ilya.monitor.sensor.service.SensorService;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

public class SensorHandler implements HandlerFunction<ServerResponse> {
    private final Mono<ServerResponse> serverResponse;

    private SensorHandler(Mono<ServerResponse> serverResponse) {
        this.serverResponse = serverResponse;
    }

    public static SensorHandler create(final SensorService sensorService, final byte period) {
        return new SensorHandler(
                ServerResponse.ok()
                        .contentType(MediaType.TEXT_EVENT_STREAM)
                        .body(
                                Flux.interval(Duration.ofSeconds(period))
//                                        .log()
                                        .map(i -> sensorService.currentValue())
                                        .doOnCancel(sensorService::cleanup)
                                        .share(),
                                SensorMessage.class
                        )
        );
    }

    @Override
    public Mono<ServerResponse> handle(final ServerRequest request) {
        return serverResponse;
    }
}
