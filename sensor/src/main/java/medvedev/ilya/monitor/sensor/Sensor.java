package medvedev.ilya.monitor.sensor;

import medvedev.ilya.monitor.proto.Protobuf.SensorValue;

import java.util.stream.Stream;

public interface Sensor {
    Stream<SensorValue> sensorValue();

    default void clear() {}
}
