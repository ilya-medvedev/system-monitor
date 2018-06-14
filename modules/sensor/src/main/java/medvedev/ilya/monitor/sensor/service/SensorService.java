package medvedev.ilya.monitor.sensor.service;

import medvedev.ilya.monitor.sensor.SensorMessage;

public interface SensorService {
    SensorMessage currentValue();

    void cleanup();
}
