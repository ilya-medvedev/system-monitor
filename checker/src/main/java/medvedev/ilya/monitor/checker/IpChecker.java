package medvedev.ilya.monitor.checker;

import medvedev.ilya.monitor.util.ExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
public class IpChecker {
    private static final Logger LOGGER = LoggerFactory.getLogger(IpChecker.class);

    private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

    private final IpService ipService;
    private final EmailNotificationService notificationService;
    private final int timeout;

    private String ip = null;

    @Autowired
    public IpChecker(
            final IpService ipService,
            final EmailNotificationService notificationService,
            @Value("${ip.checker.timeout}") final int timeout
    ) {
        this.ipService = ipService;
        this.notificationService = notificationService;
        this.timeout = timeout;
    }

    public void start() {
        final Runnable runnable = ExceptionHandler.runnableHandler(this::checkIp, LOGGER);

        executorService.scheduleWithFixedDelay(runnable, 0, timeout, TimeUnit.MINUTES);
    }

    private void checkIp() {
        final String newIp = ipService.currentIp();

        if (ip != null) {
            if (ip.equals(newIp)) {
                return;
            } else {
                notificationService.sendNotification("IP address has been changed: " + newIp);
            }
        } else {
            notificationService.sendNotification("Server is running: " + newIp);
        }

        ip = newIp;
    }
}
