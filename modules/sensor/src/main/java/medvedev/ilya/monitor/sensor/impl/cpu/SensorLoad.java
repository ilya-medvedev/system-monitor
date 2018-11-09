package medvedev.ilya.monitor.sensor.impl.cpu;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
class SensorLoad {
    private final long used;
    private final long total;
}
