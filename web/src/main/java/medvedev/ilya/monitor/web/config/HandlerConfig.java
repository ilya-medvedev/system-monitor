package medvedev.ilya.monitor.web.config;

import medvedev.ilya.monitor.sensor.Sensor;
import medvedev.ilya.monitor.sensor.cpu.Cpu;
import medvedev.ilya.monitor.sensor.mem.Mem;
import medvedev.ilya.monitor.web.handler.Handler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;

@Configuration
public class HandlerConfig {
    @Bean
    public WebSocketHandler webSocketHandler(final Cpu cpu, final Mem mem) {
        final Sensor[] sensors = new Sensor[] {cpu, mem};

        return new Handler(sensors);
    }
}
