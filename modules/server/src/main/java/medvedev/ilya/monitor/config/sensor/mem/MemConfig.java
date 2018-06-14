package medvedev.ilya.monitor.config.sensor.mem;

import medvedev.ilya.monitor.sensor.impl.mem.Mem;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;

@Configuration
@EnableConfigurationProperties(MemProperties.class)
public class MemConfig {
    @Bean
    public Mem mem(final MemProperties memProperties) {
        final File file = memProperties.getFile();

        return Mem.byFile(file);
    }
}
