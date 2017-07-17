package medvedev.ilya.monitor.controller;

import medvedev.ilya.monitor.sensor.SensorMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping(path = "/sensors", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
public class SensorController {
    private final Flux<SensorMessage> sensorFlux;

    @Autowired
    public SensorController(final Flux<SensorMessage> sensorFlux) {
        this.sensorFlux = sensorFlux;
    }

    @GetMapping
    public Flux<SensorMessage> sensors() {
        return sensorFlux;
    }
}
