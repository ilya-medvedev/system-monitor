package medvedev.ilya.monitor.sensor.impl.disk;

class SensorLoad {
    private final long read;
    private final long writen;

    SensorLoad(final long read, final long writen) {
        this.read = read;
        this.writen = writen;
    }

    long getRead() {
        return read;
    }

    long getWriten() {
        return writen;
    }
}
