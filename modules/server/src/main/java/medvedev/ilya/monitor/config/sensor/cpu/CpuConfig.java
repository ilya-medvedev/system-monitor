package medvedev.ilya.monitor.config.sensor.cpu;

import lombok.Setter;
import medvedev.ilya.monitor.sensor.impl.cpu.Cpu;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import java.io.File;

@Configuration
@ConfigurationProperties(prefix = "sensor.cpu")
@Setter
@Validated
public class CpuConfig {
    @NotNull
    private File file;

    @Bean
    public Cpu cpu() {
        return Cpu.byFile(file);
    }
}
