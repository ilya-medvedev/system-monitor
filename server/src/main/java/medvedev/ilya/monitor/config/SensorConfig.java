package medvedev.ilya.monitor.config;

import medvedev.ilya.monitor.sensor.Sensor;
import medvedev.ilya.monitor.sensor.SensorMessage;
import medvedev.ilya.monitor.sensor.impl.cpu.Cpu;
import medvedev.ilya.monitor.sensor.impl.disk.Disk;
import medvedev.ilya.monitor.sensor.impl.mem.Mem;
import medvedev.ilya.monitor.sensor.impl.net.Net;
import medvedev.ilya.monitor.sensor.service.SensorService;
import medvedev.ilya.monitor.sensor.service.impl.SensorServiceImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;

import java.io.File;
import java.time.Duration;

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
            @Value("${sensors.disk.sector}") final short size,
            @Value("${monitor.period}") final byte period
    ) {
        return Disk.byFile(file, name, size, period);
    }

    @Bean
    public Net net(
            @Value("${sensors.net.file}") final File file,
            @Value("${sensors.net.interface}") final String name,
            @Value("${monitor.period}") final byte period
    ) {
        return Net.byFile(file, name, period);
    }

    @Bean
    public SensorService sensorService(final Sensor[] sensors) {
        return new SensorServiceImpl(sensors);
    }

    @Bean
    public Flux<SensorMessage> sensorFlux(
            final SensorService sensorService,
            @Value("${monitor.period}") final byte period
    ) {
        return Flux.<SensorMessage>generate(sink -> {
            final SensorMessage sensorMessage = sensorService.currentValue();

            sink.next(sensorMessage);
        })
                .delayElements(Duration.ofSeconds(period))
                .doOnCancel(sensorService::clear)
//                .log()
                .share();
    }
}
