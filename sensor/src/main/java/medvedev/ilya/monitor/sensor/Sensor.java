package medvedev.ilya.monitor.sensor;

import medvedev.ilya.monitor.proto.Protobuf.Message.SensorInfo;

public interface Sensor {
    SensorInfo sensorInfo();

    default void clear() {}
}
