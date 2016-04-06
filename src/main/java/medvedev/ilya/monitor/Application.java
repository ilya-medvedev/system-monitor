package medvedev.ilya.monitor;

import medvedev.ilya.monitor.checker.IpChecker;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {
    public static void main(final String[] args) {
        SpringApplication.run(Application.class, args)
                .getBean(IpChecker.class)
                .start();
    }
}
