package medvedev.ilya.monitor.sensor.mem;

import medvedev.ilya.monitor.sensor.Sensor;
import medvedev.ilya.monitor.model.SensorLoad;
import medvedev.ilya.monitor.model.SensorValue;
import medvedev.ilya.monitor.sensor.mem.util.ScannerCommon;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Mem implements Sensor {
    private final File file = new File("/proc/meminfo");

    public List<SensorValue> sensorValue() {
        return sensorLoad()
                .parallelStream()
                .unordered()
                .map(sensorLoad -> {
                    final String name = sensorLoad.getName();

                    final long usage = sensorLoad.getUsed();
                    final long total = sensorLoad.getTotal();

                    final double value = 100.0 * usage / total;

                    return new SensorValue(name, value);
                })
                .collect(Collectors.toList());
    }

    private List<SensorLoad> sensorLoad() {
        final Map<String, Long> stat = new HashMap<String, Long>() {{
            /**
             * Total usable RAM (i.e., physical RAM minus a few
             * reserved bits and the kernel binary code).
             */
            put("MemTotal:", null);

            /** The sum of LowFree+HighFree. */
            put("MemFree:", null);

            /**
             * (since Linux 3.14)
             * An estimate of how much memory is available for
             * starting new applications, without swapping.
             */
            put("MemAvailable:", null);

            /**
             * Relatively temporary storage for raw disk blocks that
             * shouldn't get tremendously large (20MB or so).
             */
            put("Buffers:", null);

            /**
             * In-memory cache for files read from the disk (the page
             * cache).  Doesn't include SwapCached.
             */
            put("Cached:", null);

            /**
             * Memory that once was swapped out, is swapped back in
             * but still also is in the swap file.  (If memory
             * pressure is high, these pages don't need to be swapped
             * out again because they are already in the swap file.
             * This saves I/O.)
             */
            put("SwapCached:", null);

            /* Total amount of swap space available. */
            put("SwapTotal:", null);

            /* Amount of swap space that is currently unused. */
            put("SwapFree:", null);
        }};

        try (final Scanner scanner = new Scanner(file)) {
            do {
                final String name = scanner.next();

                if (stat.containsKey(name)) {
                    final Long value = scanner.nextLong();

                    stat.put(name, value);
                }
            } while (ScannerCommon.safetyNextLine(scanner));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        final long memFree;
        final Long available = stat.get("MemAvailable:");
        if (available != null) {
            memFree = available;
        } else {
            final long free = stat.get("MemFree:");
            final long buffers = stat.get("Buffers:");
            final long cached = stat.get("Cached:");
            final long swapCached = stat.get("SwapCached:");

            memFree = free - buffers - cached - swapCached;
        }
        final long memTotal = stat.get("MemTotal:");
        final long memUsed = memTotal - memFree;

        final long swapFree = stat.get("SwapFree:");
        final long swapTotal = stat.get("SwapTotal:");
        final long swapUsed = swapTotal - swapFree;

        final SensorLoad mem = new SensorLoad("mem", memUsed, memTotal);
        final SensorLoad swap = new SensorLoad("swap", swapUsed, swapTotal);

        return Arrays.asList(mem, swap);
    }
}
