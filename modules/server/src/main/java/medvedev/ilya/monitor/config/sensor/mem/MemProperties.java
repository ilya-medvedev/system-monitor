package medvedev.ilya.monitor.config.sensor.mem;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import java.io.File;

@Validated
@ConfigurationProperties(prefix = "sensor.mem")
public class MemProperties {
    @NotNull
    private File file;

    public File getFile() {
        return file;
    }

    public void setFile(final File file) {
        this.file = file;
    }
}
