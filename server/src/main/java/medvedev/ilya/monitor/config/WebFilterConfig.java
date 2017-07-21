package medvedev.ilya.monitor.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;

/**
 * Fix for:
 *  https://github.com/spring-projects/spring-boot/issues/9785
 * More:
 *  https://stackoverflow.com/questions/45147280/spring-webflux-how-to-forward-to-index-html-to-serve-static-content
 */
@Configuration
public class WebFilterConfig {
    @Bean
    public WebFilter indexFilter() {
        return (exchange, chain) -> {
            final ServerHttpRequest request = exchange.getRequest();

            final boolean indexRequest = request.getURI()
                    .getPath()
                    .equals("/");

            if (!indexRequest) {
                return chain.filter(exchange);
            }

            final ServerHttpRequest indexFileRequest = request.mutate()
                    .path("/index.html")
                    .build();

            final ServerWebExchange indexFileExchange = exchange.mutate()
                    .request(indexFileRequest)
                    .build();

            return chain.filter(indexFileExchange);
        };
    }
}
