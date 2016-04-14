package medvedev.ilya.monitor;

import medvedev.ilya.monitor.checker.IpChecker;
import medvedev.ilya.monitor.checker.IpCheckerConfig;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {
    public static void main(final String[] args) {
        final BeanFactory beanFactory = SpringApplication.run(Application.class, args);

        final IpCheckerConfig ipCheckerConfig = beanFactory.getBean(IpCheckerConfig.class);
        final boolean ipScheckerEnable = ipCheckerConfig.isIpScheckerEnable();

        if (ipScheckerEnable) {
            beanFactory.getBean(IpChecker.class)
                    .start();
        }
    }
}
