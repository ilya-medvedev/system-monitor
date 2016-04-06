package medvedev.ilya.monitor.model;

import medvedev.ilya.monitor.sensor.Sensor;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Message {
    private final long time = System.currentTimeMillis();

    private final List<SensorValue> sensors;

    public Message(final Sensor... sensors) {
        this.sensors = Stream.of(sensors)
                .parallel()
                .unordered()
                .map(Sensor::sensorValue)
                .map(List::parallelStream)
                .flatMap(Stream::unordered)
                .collect(Collectors.toList());
    }

    public long getTime() {
        return time;
    }

    public List<SensorValue> getSensors() {
        return sensors;
    }
}
