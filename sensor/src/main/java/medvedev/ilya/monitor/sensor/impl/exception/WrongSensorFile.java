package medvedev.ilya.monitor.sensor.impl.exception;

public class WrongSensorFile extends RuntimeException {
    private static final String WRONG_FILE = "File is wrong";

    public WrongSensorFile() {
        super(WRONG_FILE);
    }
}
