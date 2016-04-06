package medvedev.ilya.monitor.checker;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
public class IpChecker {
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
        executorService.scheduleAtFixedRate(() -> {
            final String newIp = ipService.currentIp();

            if (!newIp.equals(ip)) {
                notificationService.sendNotification(newIp);

                ip = newIp;
            }
        }, 0, timeout, TimeUnit.MINUTES);
    }
}
