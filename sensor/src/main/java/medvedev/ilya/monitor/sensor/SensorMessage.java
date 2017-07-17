package medvedev.ilya.monitor.sensor;

import java.util.List;

public class SensorMessage {
    private final Long time;
    private final List<SensorInfo> values;

    public SensorMessage(final Builder builder) {
        time = builder.time;
        values = builder.values;
    }

    public Long getTime() {
        return time;
    }

    public List<SensorInfo> getValues() {
        return values;
    }

    public static class Builder {
        private Long time;
        private List<SensorInfo> values;

        public Builder setTime(final Long time) {
            this.time = time;

            return this;
        }

        public Builder setValues(final List<SensorInfo> values) {
            this.values = values;

            return this;
        }

        public SensorMessage build() {
            return new SensorMessage(this);
        }
    }
}
