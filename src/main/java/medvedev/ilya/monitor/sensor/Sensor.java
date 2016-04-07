package medvedev.ilya.monitor.sensor;

import medvedev.ilya.monitor.model.SensorValue;

import java.util.stream.Stream;

public interface Sensor {
    Stream<SensorValue> sensorValue();
}
