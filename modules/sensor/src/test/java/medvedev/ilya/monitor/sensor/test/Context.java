package medvedev.ilya.monitor.sensor.test;

import lombok.Builder;
import lombok.Getter;
import medvedev.ilya.monitor.sensor.Sensor;

import java.io.File;

@Builder
@Getter
public class Context {
    private final File file;
    private final Sensor sensor;
}
