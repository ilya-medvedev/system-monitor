package medvedev.ilya.monitor.util;

import org.slf4j.Logger;

public class ExceptionHandler {
    public static Runnable runnableHandler(final Runnable runnable, final Logger logger) {
        return () -> {
            try {
                runnable.run();
            } catch (Exception e) {
                logger.warn("{}", runnable, e);
            }
        };
    }
}
