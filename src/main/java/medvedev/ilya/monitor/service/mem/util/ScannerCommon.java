package medvedev.ilya.monitor.service.mem.util;

import java.util.Scanner;

public class ScannerCommon {
    public static boolean safetyNextLine(final Scanner scanner) {
        if (!scanner.hasNextLine()) {
            return false;
        }

        scanner.nextLine();

        return scanner.hasNext();
    }
}
