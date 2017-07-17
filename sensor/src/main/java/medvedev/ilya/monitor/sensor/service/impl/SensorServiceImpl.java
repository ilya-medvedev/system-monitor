package medvedev.ilya.monitor.sensor.service.impl;

import medvedev.ilya.monitor.sensor.Sensor;
import medvedev.ilya.monitor.sensor.SensorInfo;
import medvedev.ilya.monitor.sensor.SensorMessage;
import medvedev.ilya.monitor.sensor.service.SensorService;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class SensorServiceImpl implements SensorService {
    private final Sensor[] sensors;

    public SensorServiceImpl(final Sensor[] sensors) {
        this.sensors = sensors;
    }

    public synchronized SensorMessage currentValue() {
        final long time = System.currentTimeMillis();

        final List<SensorInfo> sensorInfoList = Arrays.stream(sensors)
                .parallel()
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
        Arrays.stream(sensors)
                .parallel()
                .unordered()
                .forEach(Sensor::clear);
    }
}
