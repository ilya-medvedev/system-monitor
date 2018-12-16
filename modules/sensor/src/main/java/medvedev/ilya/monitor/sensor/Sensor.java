package medvedev.ilya.monitor.sensor;

public interface Sensor {
    SensorInfo sensorInfo();

    default void clean() {}
}
