package medvedev.ilya.monitor.util;

import org.slf4j.Logger;

public class HandlerUtil {
    public static Runnable exceptionHandler(final Runnable runnable, final Logger logger) {
        return () -> {
            try {
                runnable.run();
            } catch (Exception e) {
                logger.warn("{}", runnable, e);
            }
        };
    }
}
