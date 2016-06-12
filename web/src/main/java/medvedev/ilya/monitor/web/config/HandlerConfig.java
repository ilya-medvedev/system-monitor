package medvedev.ilya.monitor.web.config;

import medvedev.ilya.monitor.sensor.Sensor;
import medvedev.ilya.monitor.sensor.cpu.Cpu;
import medvedev.ilya.monitor.sensor.mem.Mem;
import medvedev.ilya.monitor.web.handler.Handler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;

import java.io.File;

@Configuration
public class HandlerConfig {
    @Bean
    public WebSocketHandler webSocketHandler(
            @Value("${sensors.cpu.file}") final File cpuFile,
            @Value("${sensors.mem.file}") final File memFile
    ) {
        final Cpu cpu = Cpu.byFile(cpuFile);
        final Mem mem = Mem.byFile(memFile);

        final Sensor[] sensors = new Sensor[] {cpu, mem};

        return new Handler(sensors);
    }
}
