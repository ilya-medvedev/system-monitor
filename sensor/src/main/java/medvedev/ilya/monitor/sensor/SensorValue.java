package medvedev.ilya.monitor.sensor;

public class SensorValue {
    private final String name;
    private final Float value;

    public SensorValue(final Builder builder) {
        name = builder.name;
        value = builder.value;
    }

    public String getName() {
        return name;
    }

    public Float getValue() {
        return value;
    }

    public static class Builder {
        private String name;
        private Float value;

        public Builder setName(final String name) {
            this.name = name;

            return this;
        }

        public Builder setValue(final Float value) {
            this.value = value;

            return this;
        }

        public SensorValue build() {
            return new SensorValue(this);
        }
    }
}
