package medvedev.ilya.monitor.config.sensor.disk;

import lombok.Setter;
import medvedev.ilya.monitor.sensor.impl.disk.Disk;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import java.io.File;

@Configuration
@ConfigurationProperties(prefix = "sensor.disk")
@Setter
@Validated
public class DiskConfig {
    @NotNull
    private File file;
    @NotNull
    private String deviceName;
    @NotNull
    private short sectorSize;

    @Bean
    public Disk disk(@Value("${monitor.period}") final byte period) {
        return Disk.byFile(file, deviceName, sectorSize, period);
    }
}
