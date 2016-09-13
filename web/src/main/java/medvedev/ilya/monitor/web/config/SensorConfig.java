package medvedev.ilya.monitor.web.config;

import medvedev.ilya.monitor.sensor.impl.cpu.Cpu;
import medvedev.ilya.monitor.sensor.impl.disk.Disk;
import medvedev.ilya.monitor.sensor.impl.mem.Mem;
import medvedev.ilya.monitor.sensor.impl.net.Net;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;

@Configuration
public class SensorConfig {
    @Bean
    public Cpu cpu(@Value("${sensors.cpu.file}") final File file) {
        return Cpu.byFile(file);
    }

    @Bean
    public Mem mem(@Value("${sensors.mem.file}") final File file) {
        return Mem.byFile(file);
    }

    @Bean
    public Disk disk(
            @Value("${sensors.disk.file}") final File file,
            @Value("${sensors.disk.device}") final String name,
            @Value("${sensors.disk.sector}") final short size
    ) {
        return Disk.byFile(file, name, size);
    }

    @Bean
    public Net net(
            @Value("${sensors.net.file}") final File file,
            @Value("${sensors.net.interface}") final String name
    ) {
        return Net.byFile(file, name);
    }
}
