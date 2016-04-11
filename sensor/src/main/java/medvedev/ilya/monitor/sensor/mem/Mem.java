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

    private static final String WRONG_FILE = "File is wrong";

    private static final String MEM_SENSOR = "mem";
    private static final String SWAP_SENSOR = "swap";

    private static final File FILE = new File("/proc/meminfo");

    public static Mem byFile() {
        Integer memTotal = null;
        Integer swapTotal = null;
        final Map<Short, Boolean> roleMap = new TreeMap<>();

        final Map<String, Short> stat = new HashMap<>();

        try (final Scanner scanner = new Scanner(FILE)) {
            short i = 0;

            do {
                final String name = scanner.next();

                switch (name) {
                    case MEM_TOTAL:
                        memTotal = scanner.nextInt();
                        break;
                    case SWAP_TOTAL:
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
            throw new RuntimeException(e);
        }

        if (memTotal == null || swapTotal == null) {
            throw new RuntimeException(WRONG_FILE);
        }

        final Short swapFree = stat.get(SWAP_FREE);

        if (swapFree == null) {
            throw new RuntimeException(WRONG_FILE);
        }

        roleMap.put(swapFree, false);

        final Short available = stat.get(MEM_AVAILABLE);
        if (available != null) {
            roleMap.put(available, true);
        } else {
            final Short free = stat.get(MEM_FREE);
            final Short buffers = stat.get(BUFFERS);
            final Short cached = stat.get(CACHED);
            final Short swapCached = stat.get(SWAP_CACHED);

            if (free == null || buffers == null || cached == null || swapCached == null) {
                throw new RuntimeException(WRONG_FILE);
            }

            roleMap.put(free, true);
            roleMap.put(buffers, true);
            roleMap.put(cached, true);
            roleMap.put(swapCached, true);
        }

        return new Mem(memTotal, swapTotal, roleMap);
    }

    private final int memTotal;
    private final int swapTotal;
    private final Map<Short, Boolean> memLineMap;

    private Mem(final int memTotal, final int swapTotal, final Map<Short, Boolean> memLineMap) {
        this.memTotal = memTotal;
        this.swapTotal = swapTotal;
        this.memLineMap = memLineMap;
    }

    public Stream<SensorValue> sensorValue() {
        int memFree = 0;
        int swapFree = 0;

        try (final Scanner scanner = new Scanner(FILE)) {
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
            throw new RuntimeException(e);
        }

        final SensorValue mem = calculateMemValue(memFree);
        final SensorValue swap = calculateSwapValue(swapFree);

        return Stream.of(mem, swap)
                .parallel()
                .unordered();
    }

    private SensorValue calculateMemValue(final int free) {
        return calculateValue(MEM_SENSOR, memTotal, free);
    }

    private SensorValue calculateSwapValue(final int free) {
        return calculateValue(SWAP_SENSOR, swapTotal, free);
    }

    private static SensorValue calculateValue(final String name, final int total, final int free) {
        final int used = total - free;

        final float value = 100.0F * used / total;

        return new SensorValue(name, value);
    }
}
