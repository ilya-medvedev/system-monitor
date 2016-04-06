package medvedev.ilya.monitor.sensor;

import medvedev.ilya.monitor.model.SensorValue;

import java.util.List;

public interface Sensor {
    List<SensorValue> sensorValue();
}
