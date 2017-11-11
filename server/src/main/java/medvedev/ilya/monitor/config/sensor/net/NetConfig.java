package medvedev.ilya.monitor.config.sensor.net;

import medvedev.ilya.monitor.sensor.impl.net.Net;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;

@Configuration
@EnableConfigurationProperties(NetProperties.class)
public class NetConfig {
    @Bean
    public Net net(final NetProperties netProperties, @Value("${monitor.period}") final byte period) {
        final File file = netProperties.getFile();
        final String interfaceName = netProperties.getInterfaceName();

        return Net.byFile(file, interfaceName, period);
    }
}
