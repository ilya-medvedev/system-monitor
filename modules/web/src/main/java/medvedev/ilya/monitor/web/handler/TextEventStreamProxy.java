package medvedev.ilya.monitor.web.handler;

import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

public class TextEventStreamProxy implements HandlerFunction<ServerResponse> {
    private final Mono<ServerResponse> serverResponse;

    private TextEventStreamProxy(final Mono<ServerResponse> serverResponse) {
        this.serverResponse = serverResponse;
    }

    public static TextEventStreamProxy byPath(final String path) {
        return new TextEventStreamProxy(
                ServerResponse.ok()
                        .contentType(MediaType.TEXT_EVENT_STREAM)
                        .body(
                                WebClient.create(path).get()
                                        .exchange()
                                        .flatMapMany(clientResponse -> clientResponse.bodyToFlux(String.class))
                                        .share(),
                                String.class
                        )
        );
    }

    @Override
    public Mono<ServerResponse> handle(final ServerRequest request) {
        return serverResponse;
    }
}
