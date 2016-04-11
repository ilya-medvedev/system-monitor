package medvedev.ilya.monitor.sensor.model;

public class SensorValue {
    private final String name;
    private final float value;

    public SensorValue(final String name, final float value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public float getValue() {
        return value;
    }
}
