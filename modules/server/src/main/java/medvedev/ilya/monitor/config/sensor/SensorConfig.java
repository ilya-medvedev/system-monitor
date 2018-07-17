package medvedev.ilya.monitor.config.sensor;

import medvedev.ilya.monitor.handler.SensorHandler;
import medvedev.ilya.monitor.sensor.Sensor;
import medvedev.ilya.monitor.sensor.service.SensorService;
import medvedev.ilya.monitor.sensor.service.impl.SensorServiceImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import java.util.List;

@Configuration
public class SensorConfig {
    @Bean
    public RouterFunction<ServerResponse> sensorRouter(final HandlerFunction<ServerResponse> sensorHandler) {
        return RouterFunctions.route(RequestPredicates.GET("/sensors"), sensorHandler);
    }

    @Bean
    public SensorHandler sensorHandler(
            final SensorService sensorService,
            @Value("${monitor.period}") final byte period
    ) {
        return SensorHandler.create(sensorService, period);
    }

    @Bean
    public SensorService sensorService(final List<Sensor> sensors) {
        return new SensorServiceImpl(sensors);
    }
}
