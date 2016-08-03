package medvedev.ilya.monitor.sensor;

import medvedev.ilya.monitor.protobuf.Protobuf.SensorMessage.SensorInfo;

public interface Sensor {
    SensorInfo sensorInfo();

    default void clear() {}
}
