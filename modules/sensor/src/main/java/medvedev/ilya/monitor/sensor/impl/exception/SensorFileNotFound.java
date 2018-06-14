package medvedev.ilya.monitor.sensor.impl.exception;

import java.io.FileNotFoundException;

public class SensorFileNotFound extends RuntimeException {
    public SensorFileNotFound(final FileNotFoundException e) {
        super(e);
    }
}
