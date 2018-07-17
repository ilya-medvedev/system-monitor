package medvedev.ilya.monitor.web.config;

import medvedev.ilya.monitor.web.handler.TextEventStreamProxy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class SensorConfig {
    @Bean
    public RouterFunction<ServerResponse> apiPathRouter(HandlerFunction<ServerResponse> sensorHandler) {
        return RouterFunctions.route(RequestPredicates.GET("/sensors"), sensorHandler);
    }

    @Bean
    public TextEventStreamProxy sensorHandler(@Value("${sensor.url}") String sensorServerPath) {
        return TextEventStreamProxy.byPath(sensorServerPath);
    }
}
