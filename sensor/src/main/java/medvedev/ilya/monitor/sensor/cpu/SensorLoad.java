package medvedev.ilya.monitor.sensor.cpu;

class SensorLoad {
    private final String name;
    private final long used;
    private final long total;

    SensorLoad(final String name, final long used, final long total) {
        this.name = name;
        this.used = used;
        this.total = total;
    }

    String getName() {
        return name;
    }

    long getUsed() {
        return used;
    }

    long getTotal() {
        return total;
    }
}
