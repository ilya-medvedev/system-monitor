package medvedev.ilya.monitor.sensor.impl.net;

class SensorLoad {
    private final long receive;
    private final long transmit;

    SensorLoad(final long receive, final long transmit) {
        this.receive = receive;
        this.transmit = transmit;
    }

    long getReceive() {
        return receive;
    }

    long getTransmit() {
        return transmit;
    }
}
