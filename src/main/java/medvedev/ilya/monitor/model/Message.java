package medvedev.ilya.monitor.model;

import medvedev.ilya.monitor.sensor.Sensor;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Message {
    private final long time = System.currentTimeMillis();

    private final List<SensorValue> sensors;

    public Message(final Sensor... sensors) {
        this.sensors = Arrays.stream(sensors)
                .parallel()
                .map(Sensor::sensorValue)
                .flatMap(List::parallelStream)
                .collect(Collectors.toList());
    }

    public long getTime() {
        return time;
    }

    public List<SensorValue> getSensors() {
        return sensors;
    }
}
