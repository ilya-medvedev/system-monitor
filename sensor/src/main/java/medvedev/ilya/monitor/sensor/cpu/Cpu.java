package medvedev.ilya.monitor.sensor.cpu;

import medvedev.ilya.monitor.sensor.model.SensorLoad;
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
    private final File file = new File("/proc/stat");

    private final Map<String, SensorLoad> preSensorLoadMap = new ConcurrentHashMap<>();

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

        try (final Scanner scanner = new Scanner(file)) {
            for (String name = scanner.next(); name.startsWith("cpu"); name = scanner.next()) {
                /** (1) Time spent in user mode. */
                final long user = scanner.nextLong();

                /**
                 * (2) Time spent in user mode with low priority
                 * (nice).
                 */
                final long nice = scanner.nextLong();

                /** (3) Time spent in system mode. */
                final long system = scanner.nextLong();

                /**
                 * (4) Time spent in the idle task.  This value
                 * hould be USER_HZ times the second entry in the
                 * /proc/uptime pseudo-file.
                 */
                final long idle = scanner.nextLong();

                /**
                 * (since Linux 2.5.41)
                 * (5) Time waiting for I/O to complete.
                 */
                final long iowait;
                if (scanner.hasNextLong()) {
                    iowait = scanner.nextLong();
                } else {
                    iowait = 0;
                }

                /**
                 * (since Linux 2.6.0-test4)
                 * (6) Time servicing interrupts.
                 */
                final long irq;
                if (scanner.hasNextLong()) {
                    irq = scanner.nextLong();
                } else {
                    irq = 0;
                }

                /**
                 * (since Linux 2.6.0-test4)
                 * (7) Time servicing softirqs.
                 */
                final long softirq;
                if (scanner.hasNextLong()) {
                    softirq = scanner.nextLong();
                } else {
                    softirq = 0;
                }

                /**
                 * (since Linux 2.6.11)
                 * (8) Stolen time, which is the time spent in
                 * other operating systems when running in a
                 * virtualized environment
                 */
                final long steal;
                if (scanner.hasNextLong()) {
                    steal = scanner.nextLong();
                } else {
                    steal = 0;
                }

                /**
                 * (since Linux 2.6.24)
                 * (9) Time spent running a virtual CPU for guest
                 * operating systems under the control of the Linux
                 * kernel.
                 */
                final long guest;
                if (scanner.hasNextLong()) {
                    guest = scanner.nextLong();
                } else {
                    guest = 0;
                }

                /**
                 * (since Linux 2.6.33)
                 * (10) Time spent running a niced guest (virtual
                 * CPU for guest operating systems under the
                 * control of the Linux kernel).
                 */
                final long guestNice;
                if (scanner.hasNextLong()) {
                    guestNice = scanner.nextLong();
                } else {
                    guestNice = 0;
                }

                scanner.nextLine();

                final long used = user + nice + system + iowait + irq + softirq + steal + guest + guestNice;
                final long total = used + idle;
                final SensorLoad sensorLoad = new SensorLoad(name, used, total);

                sensorLoadList.add(sensorLoad);
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
