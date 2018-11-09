package medvedev.ilya.monitor.config.sensor.net;

import lombok.Setter;
import medvedev.ilya.monitor.sensor.impl.net.Net;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import java.io.File;

@Configuration
@ConfigurationProperties(prefix = "sensor.net")
@Setter
@Validated
public class NetConfig {
    @NotNull
    private File file;
    @NotNull
    private String interfaceName;

    @Bean
    public Net net(@Value("${monitor.period}") final byte period) {
        return Net.byFile(file, interfaceName, period);
    }
}
