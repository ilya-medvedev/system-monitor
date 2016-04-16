package medvedev.ilya.monitor.sensor.temp;

import medvedev.ilya.monitor.proto.Protobuf.Message.SensorInfo;
import medvedev.ilya.monitor.sensor.Sensor;

import java.io.File;

public class Temp implements Sensor {
    private final File file = new File("/sys/class/thermal/thermal_zone0/temp");

    @Override
    public SensorInfo sensorInfo() {
        return null;
    }
}
