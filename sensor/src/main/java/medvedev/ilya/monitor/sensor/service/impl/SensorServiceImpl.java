package medvedev.ilya.monitor.sensor.service.impl;

import medvedev.ilya.monitor.sensor.Sensor;
import medvedev.ilya.monitor.sensor.SensorInfo;
import medvedev.ilya.monitor.sensor.SensorMessage;
import medvedev.ilya.monitor.sensor.service.SensorService;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class SensorServiceImpl implements SensorService {
    private final List<Sensor> sensors;

    public SensorServiceImpl(final List<Sensor> sensors) {
        this.sensors = sensors;
    }

    public synchronized SensorMessage currentValue() {
        final long time = System.currentTimeMillis();

        final List<SensorInfo> sensorInfoList = sensors.parallelStream()
                .unordered()
                .map(Sensor::sensorInfo)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        return new SensorMessage.Builder()
                .setTime(time)
                .setValues(sensorInfoList)
                .build();
    }

    public synchronized void cleanup() {
        sensors.parallelStream()
                .unordered()
                .forEach(Sensor::clear);
    }
}
