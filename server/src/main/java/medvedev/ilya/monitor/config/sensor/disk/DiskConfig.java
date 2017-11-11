package medvedev.ilya.monitor.config.sensor.disk;

import medvedev.ilya.monitor.sensor.impl.disk.Disk;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;

@Configuration
@EnableConfigurationProperties(DiskProperties.class)
public class DiskConfig {
    @Bean
    public Disk disk(final DiskProperties DiskProperties, @Value("${monitor.period}") final byte period) {
        final File file = DiskProperties.getFile();
        final String deviceName = DiskProperties.getDeviceName();
        final short sectorSize = DiskProperties.getSectorSize();

        return Disk.byFile(file, deviceName, sectorSize, period);
    }
}
