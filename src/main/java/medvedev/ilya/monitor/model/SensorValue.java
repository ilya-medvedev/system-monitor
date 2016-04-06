package medvedev.ilya.monitor.model;

public class SensorValue {
    private final String name;
    private final double value;

    public SensorValue(final String name, final double value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public double getValue() {
        return value;
    }
}
