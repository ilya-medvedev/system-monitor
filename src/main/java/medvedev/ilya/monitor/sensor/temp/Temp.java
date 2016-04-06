package medvedev.ilya.monitor.sensor.temp;

import medvedev.ilya.monitor.sensor.Sensor;
import medvedev.ilya.monitor.sensor.SensorValue;

import java.io.File;
import java.util.List;

public class Temp implements Sensor {
    private final File file = new File("/sys/class/thermal/thermal_zone0/temp");

    @Override
    public List<SensorValue> sensorValue() {
        return null;
    }
}
