package medvedev.ilya.monitor.service;

public class SensorLoad {
    private final String name;
    private final long used;
    private final long total;

    public SensorLoad(final String name, final long used, final long total) {
        this.name = name;
        this.used = used;
        this.total = total;
    }

    public String getName() {
        return name;
    }

    public long getUsed() {
        return used;
    }

    public long getTotal() {
        return total;
    }
}
