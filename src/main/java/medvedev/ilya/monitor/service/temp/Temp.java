package medvedev.ilya.monitor.service.temp;

import java.io.File;

public class Temp {
    private final File file = new File("/sys/class/thermal/thermal_zone0/temp");
}
