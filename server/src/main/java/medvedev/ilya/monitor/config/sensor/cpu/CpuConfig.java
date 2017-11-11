package medvedev.ilya.monitor.config.sensor.cpu;

import medvedev.ilya.monitor.sensor.impl.cpu.Cpu;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;

@Configuration
@EnableConfigurationProperties(CpuProperties.class)
public class CpuConfig {
    @Bean
    public Cpu cpu(final CpuProperties cpuProperties) {
        final File file = cpuProperties.getFile();

        return Cpu.byFile(file);
    }
}
