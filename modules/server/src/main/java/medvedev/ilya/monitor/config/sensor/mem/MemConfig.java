package medvedev.ilya.monitor.config.sensor.mem;

import lombok.Setter;
import medvedev.ilya.monitor.sensor.impl.mem.Mem;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import java.io.File;

@Configuration
@ConfigurationProperties(prefix = "sensor.mem")
@Setter
@Validated
public class MemConfig {
    @NotNull
    private File file;

    @Bean
    public Mem mem() {
        return Mem.byFile(file);
    }
}
