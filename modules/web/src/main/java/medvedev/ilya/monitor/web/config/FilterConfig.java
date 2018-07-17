package medvedev.ilya.monitor.web.config;

import medvedev.ilya.monitor.util.StringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.WebFilter;

import java.util.Objects;

@Configuration
public class FilterConfig {
    @Bean
    public WebFilter indexFilter() {
        return (exchange, chain) -> {
            final ServerHttpRequest request = exchange.getRequest();
            final String path = request.getURI()
                    .getPath();

            if (Objects.equals(StringUtils.lastChar(path), '/')) {
                return chain.filter(exchange.mutate()
                        .request(request.mutate()
                                .path(path + "index.html")
                                .build())
                        .build());
            }

            return chain.filter(exchange);
        };
    }
}
