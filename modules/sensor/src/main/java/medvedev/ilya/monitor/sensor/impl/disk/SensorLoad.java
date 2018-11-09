package medvedev.ilya.monitor.sensor.impl.disk;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
class SensorLoad {
    private final long read;
    private final long writen;
}
