package medvedev.ilya.monitor.sensor.impl.net;

import medvedev.ilya.monitor.protobuf.Protobuf.SensorMessage.SensorInfo;
import medvedev.ilya.monitor.protobuf.Protobuf.SensorMessage.SensorInfo.SensorValue;
import medvedev.ilya.monitor.sensor.Sensor;
import medvedev.ilya.monitor.sensor.impl.exception.SensorFileNotFound;
import medvedev.ilya.monitor.sensor.impl.exception.WrongSensorFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

public class Net implements Sensor {
    private final File file;

    private final short receive;
    private final short transmit;

    private final short interfaces;

    private final Map<String, SensorLoad> preSensorLoadMap = new ConcurrentHashMap<>();

    private Net(final File file, final short receive, final short transmit, final short interfaces) {
        this.file = file;
        this.receive = receive;
        this.transmit = transmit;
        this.interfaces = interfaces;
    }

    public static Net byFile(final File file) {
        Short receive = null;
        Short transmit = null;

        short interfaces = 0;

        try (final Scanner scanner = new Scanner(file)) {
            scanner.nextLine();

            for (short i = 0; scanner.hasNext() && transmit == null;) {
                final String[] next = scanner.next()
                        .split("\\|");

                for (final String nextColumn : next) {
                    if (!nextColumn.isEmpty()) {
                        if (nextColumn.equals("bytes")) {
                            if (receive == null) {
                                receive = i;
                            } else {
                                transmit = i;
                            }
                        }

                        i++;
                    }
                }
            }

            if (transmit == null) {
                throw new WrongSensorFile();
            }

            while (scanner.hasNextLine()) {
                scanner.nextLine();

                if (scanner.hasNext()) {
                    interfaces++;
                }
            }
        } catch (final FileNotFoundException e) {
            throw new SensorFileNotFound(e);
        }

        return new Net(file, receive, transmit, interfaces);
    }

    @Override
    public SensorInfo sensorInfo() {
        final SensorInfo.Builder builder = SensorInfo.newBuilder()
                .setName("net");

        try (final Scanner scanner = new Scanner(file)) {
            scanner.nextLine();

            for (short interfaces = 0; interfaces < this.interfaces; interfaces++) {
                scanner.nextLine();

                final String name = scanner.next();

                short column = 0;

                while (column < this.receive) {
                    scanner.next();

                    column++;
                }
                final long receive = scanner.nextLong();

                column++;

                while (column < this.transmit) {
                    scanner.next();

                    column++;
                }
                final long transmit = scanner.nextLong();

                final List<SensorValue> sensorValues = calculateValue(name, receive, transmit);

                if (sensorValues != null) {
                    builder.addAllValue(sensorValues);
                }
            }
        } catch (final FileNotFoundException e) {
            throw new SensorFileNotFound(e);
        }

        return builder.build();
    }

    private List<SensorValue> calculateValue(final String name, final long receive, final long transmit) {
        final SensorLoad sensorLoad = new SensorLoad(receive, transmit);

        final SensorLoad preSensorLoad = preSensorLoadMap.put(name, sensorLoad);

        if (preSensorLoad == null) {
            return null;
        }

        final long preReceive = preSensorLoad.getReceive();
        final long preTransmit = preSensorLoad.getTransmit();

        final long receiveValue = receive - preReceive;
        final long transmitValue = transmit - preTransmit;

        final SensorValue receiveSensorValue = SensorValue.newBuilder()
                .setName(name + " Down")
                .setValue(receiveValue)
                .build();

        final SensorValue transmitSensorValue = SensorValue.newBuilder()
                .setName(name + " Up")
                .setValue(transmitValue)
                .build();

        return Arrays.asList(receiveSensorValue, transmitSensorValue);
    }

    @Override
    public void clear() {
        preSensorLoadMap.clear();
    }
}
