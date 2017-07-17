package medvedev.ilya.monitor.sensor;

import java.util.List;

public class SensorInfo {
    private final String name;
    private final List<SensorValue> values;

    public SensorInfo(final Builder builder) {
        name = builder.name;
        values = builder.values;
    }

    public String getName() {
        return name;
    }

    public List<SensorValue> getValues() {
        return values;
    }

    public static class Builder {
        private String name;
        private List<SensorValue> values;

        public Builder setName(final String name) {
            this.name = name;

            return this;
        }

        public Builder setValues(final List<SensorValue> values) {
            this.values = values;

            return this;
        }

        public SensorInfo build() {
            return new SensorInfo(this);
        }
    }
}
