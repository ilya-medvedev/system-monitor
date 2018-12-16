package medvedev.ilya.monitor.sensor.impl.net;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import medvedev.ilya.monitor.sensor.Sensor;
import medvedev.ilya.monitor.sensor.SensorInfo;
import medvedev.ilya.monitor.sensor.SensorValue;
import medvedev.ilya.monitor.sensor.impl.exception.SensorFileNotFound;
import medvedev.ilya.monitor.sensor.impl.exception.WrongSensorFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class Net implements Sensor {
    private final File file;

    private final short receive;
    private final short transmit;

    private final short line;

    private final byte period;

    private SensorLoad preSensorLoad = null;

    public static Net byFile(final File file, final String name, final byte period) {
        Short receive = null;
        Short transmit = null;

        Short line = null;

        try (final Scanner scanner = new Scanner(file)) {
            if (!scanner.hasNextLine()) {
                throw new WrongSensorFile();
            }

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

            for (short i = 0; scanner.hasNextLine() && line == null; i++) {
                scanner.nextLine();

                if (!scanner.hasNext()) {
                    throw new WrongSensorFile();
                }

                final String next = scanner.next();
                final int length = next.length();
                final boolean equals = next.substring(0, length - 1)
                        .equals(name);

                if (equals) {
                    line = i;
                }
            }

            if (line == null) {
                throw new WrongSensorFile();
            }
        } catch (final FileNotFoundException e) {
            throw new SensorFileNotFound(e);
        }

        return new Net(file, receive, transmit, line, period);
    }

    @Override
    public SensorInfo sensorInfo() {
        final long receive;
        final long transmit;

        try (final Scanner scanner = new Scanner(file)) {
            scanner.nextLine();
            scanner.nextLine();

            for (short interfaceLine = 0; interfaceLine < this.line; interfaceLine++) {
                scanner.nextLine();
            }

            short column = 0;

            while (column < this.receive) {
                scanner.next();

                column++;
            }
            receive = scanner.nextLong();

            column++;

            while (column < this.transmit) {
                scanner.next();

                column++;
            }
            transmit = scanner.nextLong();
        } catch (final FileNotFoundException e) {
            throw new SensorFileNotFound(e);
        }

        final SensorLoad preSensorLoad = this.preSensorLoad;
        this.preSensorLoad = new SensorLoad(receive, transmit);

        if (preSensorLoad == null) {
            return null;
        }

        final long preReceive = preSensorLoad.getReceive();
        final long preTransmit = preSensorLoad.getTransmit();

        final float receiveValue = ((float) (receive - preReceive)) / period;
        final float transmitValue = ((float) (transmit - preTransmit)) / period;

        final SensorValue downSensorValue = SensorValue.builder()
                .name("down")
                .value(receiveValue)
                .build();
        final SensorValue upSensorValue = SensorValue.builder()
                .name("up")
                .value(transmitValue)
                .build();
        final List<SensorValue> sensorValues = Arrays.asList(downSensorValue, upSensorValue);

        return SensorInfo.builder()
                .name("net")
                .values(sensorValues)
                .build();
    }

    @Override
    public void clean() {
        preSensorLoad = null;
    }
}
