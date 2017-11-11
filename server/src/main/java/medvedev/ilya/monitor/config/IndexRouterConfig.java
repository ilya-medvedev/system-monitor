package medvedev.ilya.monitor.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Configuration
public class IndexRouterConfig {
    @Bean
    public RouterFunction<ServerResponse> indexRouter(@Value("classpath:/static/index.html") final Resource index) {
        final Mono<Resource> indexResource = Mono.just(index)
                .cache();

        final Mono<ServerResponse> indexResponse = ServerResponse.ok()
                .body(indexResource, Resource.class)
                .cache();

        return RouterFunctions.route(RequestPredicates.GET("/"), request -> indexResponse);
    }
}
