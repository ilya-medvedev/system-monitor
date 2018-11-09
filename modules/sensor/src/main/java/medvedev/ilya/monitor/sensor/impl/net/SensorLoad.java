package medvedev.ilya.monitor.sensor.impl.net;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
class SensorLoad {
    private final long receive;
    private final long transmit;
}
