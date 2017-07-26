package medvedev.ilya.monitor;

import medvedev.ilya.monitor.properties.SensorProperties;
import medvedev.ilya.monitor.properties.SensorProperties.CpuProperties;
import medvedev.ilya.monitor.properties.SensorProperties.DiskProperties;
import medvedev.ilya.monitor.properties.SensorProperties.MemProperties;
import medvedev.ilya.monitor.properties.SensorProperties.NetProperties;
import medvedev.ilya.monitor.sensor.Sensor;
import medvedev.ilya.monitor.sensor.SensorMessage;
import medvedev.ilya.monitor.sensor.impl.cpu.Cpu;
import medvedev.ilya.monitor.sensor.impl.disk.Disk;
import medvedev.ilya.monitor.sensor.impl.mem.Mem;
import medvedev.ilya.monitor.sensor.impl.net.Net;
import medvedev.ilya.monitor.sensor.service.SensorService;
import medvedev.ilya.monitor.sensor.service.impl.SensorServiceImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.File;
import java.time.Duration;
import java.util.function.Supplier;
import java.util.stream.Stream;

@SpringBootApplication
@EnableConfigurationProperties(SensorProperties.class)
public class Application {
    public static void main(final String[] args) {
        SpringApplication.run(Application.class, args);
    }

    private HandlerFunction<ServerResponse> indexHandler(final Resource index) {
        final Mono<Resource> indexResource = Mono.just(index)
                .cache();

        final Mono<ServerResponse> indexResponse = ServerResponse.ok()
                .body(indexResource, Resource.class)
                .cache();

        return request -> indexResponse;
    }

    private Sensor cpuSensor(final CpuProperties properties) {
        final File file = properties.getFile();

        return Cpu.byFile(file);
    }

    private Sensor memSensor(final MemProperties properties) {
        final File file = properties.getFile();

        return Mem.byFile(file);
    }

    private Sensor diskSensor(final DiskProperties diskProperties, final byte period) {
        final File file = diskProperties.getFile();
        final String deviceName = diskProperties.getDeviceName();
        final short sectorSize = diskProperties.getSectorSize();

        return Disk.byFile(file, deviceName, sectorSize, period);
    }

    private Sensor netSensor(final NetProperties properties, final byte period) {
        final File file = properties.getFile();
        final String interfaceName = properties.getInterfaceName();

        return Net.byFile(file, interfaceName, period);
    }

    private HandlerFunction<ServerResponse> sensorHandler(final byte period, final SensorProperties sensorProperties) {
        final Sensor[] sensors = Stream.<Supplier<Sensor>>of(
                () -> {
                    final CpuProperties cpuProperties = sensorProperties.getCpu();

                    return cpuSensor(cpuProperties);
                },
                () -> {
                    final MemProperties memProperties = sensorProperties.getMem();

                    return memSensor(memProperties);
                },
                () -> {
                    final DiskProperties diskProperties = sensorProperties.getDisk();

                    return diskSensor(diskProperties, period);
                },
                () -> {
                    final NetProperties netProperties = sensorProperties.getNet();

                    return netSensor(netProperties, period);
                }
        )
                .parallel()
                .unordered()
                .map(Supplier::get)
                .toArray(Sensor[]::new);

        final SensorService sensorService = new SensorServiceImpl(sensors);

        final Duration duration = Duration.ofSeconds(period);

        final Flux<SensorMessage> sensorFlux = Flux.interval(duration)
//                .log()
                .map(i -> sensorService.currentValue())
                .doOnCancel(sensorService::cleanup)
                .share();

        final Mono<ServerResponse> sensorResponse = ServerResponse.ok()
                .contentType(MediaType.TEXT_EVENT_STREAM)
                .body(sensorFlux, SensorMessage.class)
                .cache();

        return request -> sensorResponse;
    }

    @Bean
    public RouterFunction router(
            @Value("classpath:/static/index.html") final Resource index,
            @Value("${monitor.period}") final byte period,
            final SensorProperties sensorProperties
    ) {
        final HandlerFunction<ServerResponse> indexHandler = indexHandler(index);
        final HandlerFunction<ServerResponse> sensorHandler = sensorHandler(period, sensorProperties);

        return RouterFunctions.route(RequestPredicates.GET("/"), indexHandler)
                .andRoute(RequestPredicates.GET("/sensors"), sensorHandler);
    }
}
