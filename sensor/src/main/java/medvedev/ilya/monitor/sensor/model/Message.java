package medvedev.ilya.monitor.sensor.model;

import medvedev.ilya.monitor.sensor.Sensor;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Message {
    public static Message bySensors(final Sensor... sensors) {
        final List<SensorValue> sensorValue = Arrays.stream(sensors)
                .parallel()
                .unordered()
                .flatMap(Sensor::sensorValue)
                .collect(Collectors.toList());

        return new Message(sensorValue);
    }

    private final long time = System.currentTimeMillis();

    private final List<SensorValue> sensors;

    private Message(final List<SensorValue> sensors) {
        this.sensors = sensors;
    }

    public long getTime() {
        return time;
    }

    public List<SensorValue> getSensors() {
        return sensors;
    }
}
