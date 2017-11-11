package medvedev.ilya.monitor.config.sensor;

import medvedev.ilya.monitor.sensor.Sensor;
import medvedev.ilya.monitor.sensor.service.SensorService;
import medvedev.ilya.monitor.sensor.service.impl.SensorServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SensorServiceConfig {
    @Bean
    public SensorService sensorService(final List<Sensor> sensors) {
        return new SensorServiceImpl(sensors);
    }
}
