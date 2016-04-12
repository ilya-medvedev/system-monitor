package medvedev.ilya.monitor.sensor.cpu;

import medvedev.ilya.monitor.proto.Protobuf.SensorValue;
import medvedev.ilya.monitor.sensor.Sensor;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

public class Cpu implements Sensor {
    private static final File FILE = new File("/proc/stat");

    public static Cpu byFile() {
        short cpu = 0;
        short stats = 0;

        try (final Scanner scanner = new Scanner(FILE)) {
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
            throw new RuntimeException(e);
        }

        return new Cpu(cpu, stats);
    }

    private final short cpu;
    private final short stats;

    private final Map<String, SensorLoad> preSensorLoadMap = new ConcurrentHashMap<>();

    private Cpu(final short cpu, final short stats) {
        this.cpu = cpu;
        this.stats = stats;
    }

    public Stream<SensorValue> sensorValue() {
        return sensorLoad()
                .parallelStream()
                .unordered()
                .map(this::calculateResult)
                .filter(Cpu::resultFilter);
    }

    private SensorValue calculateResult(final SensorLoad sensorLoad) {
        final String name = sensorLoad.getName();
        final SensorLoad preSensorLoad = preSensorLoadMap.put(name, sensorLoad);

        if (preSensorLoad == null)
            return null;

        final long used = sensorLoad.getUsed();
        final long preUsed = preSensorLoad.getUsed();

        final float value;
        if (preUsed == used) {
            value = 0F;
        } else {
            final long total = sensorLoad.getTotal();
            final long preTotal = preSensorLoad.getTotal();

            value = 100.0F * (used - preUsed) / (total - preTotal);
        }

        return SensorValue.newBuilder()
                .setName(name)
                .setValue(value)
                .build();
    }

    private static boolean resultFilter(final SensorValue sensorValue) {
        return sensorValue != null;
    }

    private List<SensorLoad> sensorLoad() {
        final List<SensorLoad> sensorLoadList = new ArrayList<>();

        try (final Scanner scanner = new Scanner(FILE)) {
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

                final long total = used + idle;
                final SensorLoad sensorLoad = new SensorLoad(name, used, total);

                sensorLoadList.add(sensorLoad);

                scanner.nextLine();
            }
        } catch (final FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        return sensorLoadList;
    }

    @Override
    public void clear() {
        preSensorLoadMap.clear();
    }
}
