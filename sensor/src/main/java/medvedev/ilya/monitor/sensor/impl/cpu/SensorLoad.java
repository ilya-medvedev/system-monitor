package medvedev.ilya.monitor.sensor.impl.cpu;

class SensorLoad {
    private final long used;
    private final long total;

    SensorLoad(final long used, final long total) {
        this.used = used;
        this.total = total;
    }

    long getUsed() {
        return used;
    }

    long getTotal() {
        return total;
    }
}
