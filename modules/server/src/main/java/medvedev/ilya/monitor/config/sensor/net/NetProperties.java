package medvedev.ilya.monitor.config.sensor.net;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import java.io.File;

@Validated
@ConfigurationProperties(prefix = "sensor.net")
public class NetProperties {
    @NotNull
    private File file;

    @NotNull
    private String interfaceName;

    public File getFile() {
        return file;
    }

    public void setFile(final File file) {
        this.file = file;
    }

    public String getInterfaceName() {
        return interfaceName;
    }

    public void setInterfaceName(final String interfaceName) {
        this.interfaceName = interfaceName;
    }
}
