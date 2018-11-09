package medvedev.ilya.monitor.sensor;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class SensorInfo {
    private final String name;
    private final List<SensorValue> values;
}
