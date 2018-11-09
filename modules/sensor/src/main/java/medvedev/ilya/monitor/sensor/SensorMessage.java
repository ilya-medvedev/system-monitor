package medvedev.ilya.monitor.sensor;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class SensorMessage {
    private final Long time;
    private final List<SensorInfo> values;
}
