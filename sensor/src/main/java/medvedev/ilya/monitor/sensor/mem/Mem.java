package medvedev.ilya.monitor.sensor.mem;

import medvedev.ilya.monitor.sensor.Sensor;
import medvedev.ilya.monitor.sensor.model.SensorValue;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.stream.Stream;

public class Mem implements Sensor {
    /**
     * Total usable RAM (i.e., physical RAM minus a few
     * reserved bits and the kernel binary code).
     */
    private static final String MEM_TOTAL = "MemTotal:";

    /** The sum of LowFree+HighFree. */
    private static final String MEM_FREE = "MemFree:";

    /**
     * (since Linux 3.14)
     * An estimate of how much memory is available for
     * starting new applications, without swapping.
     */
    private static final String MEM_AVAILABLE = "MemAvailable:";

    /**
     * Relatively temporary storage for raw disk blocks that
     * shouldn't get tremendously large (20MB or so).
     */
    private static final String BUFFERS = "Buffers:";

    /**
     * In-memory cache for files read from the disk (the page
     * cache).  Doesn't include SwapCached.
     */
    private static final String CACHED = "Cached:";

    /**
     * Memory that once was swapped out, is swapped back in
     * but still also is in the swap file.  (If memory
     * pressure is high, these pages don't need to be swapped
     * out again because they are already in the swap file.
     * This saves I/O.)
     */
    private static final String SWAP_CACHED = "SwapCached:";

    /* Total amount of swap space available. */
    private static final String SWAP_TOTAL = "SwapTotal:";

    /* Amount of swap space that is currently unused. */
    private static final String SWAP_FREE = "SwapFree:";

    private static final File FILE = new File("/proc/meminfo");
    private final Map<Short, Role> roleMap = new TreeMap<>();

    public Mem() {
        final Map<String, Short> stat = new HashMap<>();

        try (final Scanner scanner = new Scanner(FILE)) {
            short i = 0;

            do {
                final String name = scanner.next();

                stat.put(name, i);

                if (scanner.hasNextLine()) {
                    scanner.nextLine();

                    i++;
                }
            } while (scanner.hasNext());
        } catch (final FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        final short memTotal = stat.get(MEM_TOTAL);
        final short swapTotal = stat.get(SWAP_TOTAL);
        final short swapFree = stat.get(SWAP_FREE);

        roleMap.put(memTotal, Role.MEM_TOTAL);
        roleMap.put(swapTotal, Role.SWAP_TOTAL);
        roleMap.put(swapFree, Role.SWAP_FREE);

        final Short available = stat.get(MEM_AVAILABLE);
        if (available != null) {
            roleMap.put(available, Role.MEM_FREE);
        } else {
            final short free = stat.get(MEM_FREE);
            final short buffers = stat.get(BUFFERS);
            final short cached = stat.get(CACHED);
            final short swapCached = stat.get(SWAP_CACHED);

            roleMap.put(free, Role.MEM_FREE);
            roleMap.put(buffers, Role.MEM_FREE);
            roleMap.put(cached, Role.MEM_FREE);
            roleMap.put(swapCached, Role.MEM_FREE);
        }
    }

    public Stream<SensorValue> sensorValue() {
        int memTotal = 0;
        int memFree = 0;
        int swapTotal = 0;
        int swapFree = 0;

        try (final Scanner scanner = new Scanner(FILE)) {
            short lineIndex = 0;

            for (final Map.Entry<Short, Role> integerStringEntry : roleMap.entrySet()) {
                final short line = integerStringEntry.getKey();

                while (lineIndex < line) {
                    scanner.nextLine();

                    lineIndex++;
                }

                scanner.next();

                final int value = scanner.nextInt();
                final Role role = integerStringEntry.getValue();

                switch (role) {
                    case MEM_TOTAL:
                        memTotal += value;
                        break;
                    case MEM_FREE:
                        memFree += value;
                        break;
                    case SWAP_TOTAL:
                        swapTotal += value;
                        break;
                    case SWAP_FREE:
                        swapFree += value;
                        break;
                }
            }
        } catch (final FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        final SensorValue mem = calculateValue("mem", memTotal, memFree);
        final SensorValue swap = calculateValue("swap", swapTotal, swapFree);

        return Stream.of(mem, swap)
                .parallel()
                .unordered();
    }

    private static SensorValue calculateValue(final String name, final int total, final int free) {
        final int used = total - free;

        final float value = 100.0F * used / total;

        return new SensorValue(name, value);
    }
}
