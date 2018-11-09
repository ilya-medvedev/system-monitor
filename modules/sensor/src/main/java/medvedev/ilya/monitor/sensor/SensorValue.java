package medvedev.ilya.monitor.sensor;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class SensorValue {
    private final String name;
    private final Float value;
}
