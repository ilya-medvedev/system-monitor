package medvedev.ilya.monitor.sensor.cpu;

import medvedev.ilya.monitor.sensor.model.SensorValue;
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
    private final int cpu;
    private final int stats;

    private final Map<String, SensorLoad> preSensorLoadMap = new ConcurrentHashMap<>();

    public Cpu() {
        int cpu = 0;
        int stats = 0;

        try (final Scanner scanner = new Scanner(FILE)) {
            for (int i = 0; i < 6; i++) {
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

        final double value;
        if (preUsed == used) {
            value = 0D;
        } else {
            final long total = sensorLoad.getTotal();
            final long preTotal = preSensorLoad.getTotal();

            value = 100.0 * (used - preUsed) / (total - preTotal);
        }

        return new SensorValue(name, value);
    }

    private static boolean resultFilter(final SensorValue sensorValue) {
        return sensorValue != null;
    }

    private List<SensorLoad> sensorLoad() {
        final List<SensorLoad> sensorLoadList = new ArrayList<>();

        try (final Scanner scanner = new Scanner(FILE)) {
            for (int cpu = -1; cpu < this.cpu; cpu++) {
                final String name = scanner.next();

                long used = scanner.nextLong();

                for (int stats = 0; stats < 2; stats++) {
                    used += scanner.nextLong();
                }

                final long idle = scanner.nextLong();

                for (int stats = 0; stats < this.stats; stats++) {
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

    public void clear() {
        preSensorLoadMap.clear();
    }
}
