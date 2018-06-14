package medvedev.ilya.monitor.sensor.impl.disk;

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

public class Disk implements Sensor {
    private final File file;
    private final short line;
    private final float rate;

    private SensorLoad preSensorLoad = null;

    private Disk(final File file, final short line, final float rate) {
        this.file = file;
        this.line = line;
        this.rate = rate;
    }

    public static Disk byFile(final File file, final String name, final short size, final byte period) {
        Short line = null;

        try (final Scanner scanner = new Scanner(file)) {
            for (short i = 0; line == null; i++) {
                for (int wordI = 0; wordI < 2; wordI++) {
                    if (!scanner.hasNext()) {
                        throw new WrongSensorFile();
                    }

                    scanner.next();
                }

                final boolean equals = scanner.next()
                        .equals(name);

                if (equals) {
                    line = i;
                }

                if (!scanner.hasNextLine()) {
                    throw new WrongSensorFile();
                }

                scanner.nextLine();
            }
        } catch (final FileNotFoundException e) {
            throw new SensorFileNotFound(e);
        }

        final float rate = ((float) (size)) / period;

        return new Disk(file, line, rate);
    }

    @Override
    public SensorInfo sensorInfo() {
        final long read;
        final long written;

        try (final Scanner scanner = new Scanner(file)) {
            for (int i = 0; i < line; i++) {
                scanner.nextLine();
            }

            short column = 0;

            while (column < 5) {
                scanner.next();

                column++;
            }
            read = scanner.nextLong();

            column++;

            while (column < 9) {
                scanner.next();

                column++;
            }
            written = scanner.nextLong();
        } catch (final FileNotFoundException e) {
            throw new SensorFileNotFound(e);
        }

        final SensorLoad preSensorLoad = this.preSensorLoad;
        this.preSensorLoad = new SensorLoad(read, written);

        if (preSensorLoad == null) {
            return null;
        }

        final long preRead = preSensorLoad.getRead();
        final long preWriten = preSensorLoad.getWriten();

        final float readValue = (read - preRead) * rate;
        final float writenValue = (written - preWriten) * rate;

        final SensorValue readSensorValue = new SensorValue.Builder()
                .setName("read")
                .setValue(readValue)
                .build();
        final SensorValue writeSensorValue = new SensorValue.Builder()
                .setName("write")
                .setValue(writenValue)
                .build();
        final List<SensorValue> sensorValues = Arrays.asList(readSensorValue, writeSensorValue);

        return new SensorInfo.Builder()
                .setName("disk")
                .setValues(sensorValues)
                .build();
    }

    @Override
    public void clear() {
        preSensorLoad = null;
    }
}
