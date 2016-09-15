package medvedev.ilya.monitor.sensor.impl.cpu;

import medvedev.ilya.monitor.protobuf.SensorMessage.SensorInfo;
import medvedev.ilya.monitor.protobuf.SensorMessage.SensorInfo.SensorValue;
import medvedev.ilya.monitor.sensor.Sensor;
import medvedev.ilya.monitor.sensor.impl.exception.SensorFileNotFound;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

public class Cpu implements Sensor {
    private final File file;
    private final short cpu;
    private final short stats;

    private final Map<String, SensorLoad> preSensorLoadMap = new ConcurrentHashMap<>();

    private Cpu(final File file, final short cpu, final short stats) {
        this.file = file;
        this.cpu = cpu;
        this.stats = stats;
    }

    public static Cpu byFile(final File file) {
        short cpu = 0;
        short stats = 0;

        try (final Scanner scanner = new Scanner(file)) {
            for (byte i = 0; i < 6; i++) {
                scanner.next();
            }

            while (stats < 6 && scanner.hasNextLong()) {
                scanner.next();

                stats++;
            }

            scanner.nextLine();
            String name = scanner.next();
            while (name.startsWith("cpu")) {
                cpu++;

                scanner.nextLine();
                name = scanner.next();
            }
        } catch (final FileNotFoundException e) {
            throw new SensorFileNotFound(e);
        }

        return new Cpu(file, cpu, stats);
    }

    @Override
    public SensorInfo sensorInfo() {
        final SensorInfo.Builder builder = SensorInfo.newBuilder()
                .setName("cpu");

        try (final Scanner scanner = new Scanner(file)) {
            for (short cpu = -1; cpu < this.cpu; cpu++) {
                final String name = scanner.next();

                long used = scanner.nextLong();

                for (short stats = 0; stats < 2; stats++) {
                    used += scanner.nextLong();
                }

                final long idle = scanner.nextLong();

                for (short stats = 0; stats < this.stats; stats++) {
                    used += scanner.nextLong();
                }

                final SensorValue sensorValue = calculateValue(name, used, idle);

                if (sensorValue != null) {
                    builder.addValue(sensorValue);
                }

                scanner.nextLine();
            }
        } catch (final FileNotFoundException e) {
            throw new SensorFileNotFound(e);
        }

        return builder.build();
    }

    private SensorValue calculateValue(final String name, final long used, final long idle) {
        final long total = used + idle;
        final SensorLoad sensorLoad = new SensorLoad(used, total);

        final SensorLoad preSensorLoad = preSensorLoadMap.put(name, sensorLoad);

        if (preSensorLoad == null) {
            return null;
        }

        final long preUsed = preSensorLoad.getUsed();

        final float value;
        if (preUsed == used) {
            value = 0F;
        } else {
            final long preTotal = preSensorLoad.getTotal();

            value = 100.0F * (used - preUsed) / (total - preTotal);
        }

        return SensorValue.newBuilder()
                .setName(name)
                .setValue(value)
                .build();
    }

    @Override
    public void clear() {
        preSensorLoadMap.clear();
    }
}
