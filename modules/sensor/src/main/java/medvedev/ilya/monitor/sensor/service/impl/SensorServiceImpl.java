package medvedev.ilya.monitor.sensor.service.impl;

import lombok.RequiredArgsConstructor;
import medvedev.ilya.monitor.sensor.Sensor;
import medvedev.ilya.monitor.sensor.SensorInfo;
import medvedev.ilya.monitor.sensor.SensorMessage;
import medvedev.ilya.monitor.sensor.service.SensorService;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class SensorServiceImpl implements SensorService {
    private final List<Sensor> sensors;

    public synchronized SensorMessage currentValue() {
        final long time = System.currentTimeMillis();

        final List<SensorInfo> sensorInfoList = sensors.parallelStream()
                .unordered()
                .map(Sensor::sensorInfo)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        return SensorMessage.builder()
                .time(time)
                .values(sensorInfoList)
                .build();
    }

    public synchronized void cleanup() {
        sensors.parallelStream()
                .unordered()
                .forEach(Sensor::clear);
    }
}
