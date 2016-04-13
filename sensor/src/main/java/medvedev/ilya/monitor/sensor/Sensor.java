package medvedev.ilya.monitor.sensor;

import medvedev.ilya.monitor.proto.Protobuf.SensorValue.Builder;

import java.util.stream.Stream;

public interface Sensor {
    Stream<Builder> sensorValue();

    default void clear() {}
}
