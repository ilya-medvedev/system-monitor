package medvedev.ilya.monitor.sensor.impl.exception;

public class WrongSensorFile extends RuntimeException {
    public WrongSensorFile() {
        super("File is wrong");
    }
}
