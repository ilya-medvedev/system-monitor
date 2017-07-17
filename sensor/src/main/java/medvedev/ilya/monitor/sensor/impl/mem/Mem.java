package medvedev.ilya.monitor.sensor.impl.mem;

import medvedev.ilya.monitor.sensor.Sensor;
import medvedev.ilya.monitor.sensor.SensorInfo;
import medvedev.ilya.monitor.sensor.SensorValue;
import medvedev.ilya.monitor.sensor.impl.exception.SensorFileNotFound;
import medvedev.ilya.monitor.sensor.impl.exception.WrongSensorFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

public class Mem implements Sensor {
    private final File file;
    private final int memTotal;
    private final int swapTotal;
    private final Map<Short, Boolean> memLineMap;

    private Mem(final File file, final int memTotal, final int swapTotal, final Map<Short, Boolean> memLineMap) {
        this.file = file;
        this.memTotal = memTotal;
        this.swapTotal = swapTotal;
        this.memLineMap = memLineMap;
    }

    public static Mem byFile(final File file) {
        Integer memTotal = null;
        Integer swapTotal = null;
        final Map<Short, Boolean> roleMap = new TreeMap<>();

        final Map<String, Short> stat = new HashMap<>();

        try (final Scanner scanner = new Scanner(file)) {
            short i = 0;

            do {
                final String name = scanner.next();

                switch (name) {
                    case "MemTotal:":
                        memTotal = scanner.nextInt();
                        break;
                    case "SwapTotal:":
                        swapTotal = scanner.nextInt();
                        break;
                    default:
                        stat.put(name, i);
                        break;
                }

                if (scanner.hasNextLine()) {
                    scanner.nextLine();

                    i++;
                }
            } while (scanner.hasNext());
        } catch (final FileNotFoundException e) {
            throw new SensorFileNotFound(e);
        }

        if (memTotal == null || swapTotal == null) {
            throw new WrongSensorFile();
        }

        final Short swapFree = stat.get("SwapFree:");

        if (swapFree == null) {
            throw new WrongSensorFile();
        }

        roleMap.put(swapFree, false);

        final Short available = stat.get("MemAvailable:");
        if (available != null) {
            roleMap.put(available, true);
        } else {
            final Short free = stat.get("MemFree:");
            final Short buffers = stat.get("Buffers:");
            final Short cached = stat.get("Cached:");
            final Short swapCached = stat.get("SwapCached:");

            if (free == null || buffers == null || cached == null || swapCached == null) {
                throw new WrongSensorFile();
            }

            roleMap.put(free, true);
            roleMap.put(buffers, true);
            roleMap.put(cached, true);
            roleMap.put(swapCached, true);
        }

        return new Mem(file, memTotal, swapTotal, roleMap);
    }

    @Override
    public SensorInfo sensorInfo() {
        int memFree = 0;
        int swapFree = 0;

        try (final Scanner scanner = new Scanner(file)) {
            short lineIndex = 0;

            for (final Map.Entry<Short, Boolean> memLine : memLineMap.entrySet()) {
                final short line = memLine.getKey();

                while (lineIndex < line) {
                    scanner.nextLine();

                    lineIndex++;
                }

                scanner.next();

                final int value = scanner.nextInt();
                final boolean mem = memLine.getValue();

                if (mem) {
                    memFree += value;
                } else {
                    swapFree += value;
                }
            }
        } catch (final FileNotFoundException e) {
            throw new SensorFileNotFound(e);
        }

        final SensorValue mem = calculateValue("mem", memTotal, memFree);
        final SensorValue swap = calculateValue("swap", swapTotal, swapFree);
        final List<SensorValue> sensorValues = Arrays.asList(mem, swap);

        return new SensorInfo.Builder()
                .setName("mem")
                .setValues(sensorValues)
                .build();
    }

    private static SensorValue calculateValue(final String name, final int total, final int free) {
        final int used = total - free;

        final float value = 100.0F * used / total;

        return new SensorValue.Builder()
                .setName(name)
                .setValue(value)
                .build();
    }
}
