package medvedev.ilya.monitor.config.sensor.disk;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import java.io.File;

@Validated
@ConfigurationProperties(prefix = "sensor.disk")
public class DiskProperties {
    @NotNull
    private File file;

    @NotNull
    private String deviceName;

    @NotNull
    private short sectorSize;

    public File getFile() {
        return file;
    }

    public void setFile(final File file) {
        this.file = file;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(final String deviceName) {
        this.deviceName = deviceName;
    }

    public short getSectorSize() {
        return sectorSize;
    }

    public void setSectorSize(final short sectorSize) {
        this.sectorSize = sectorSize;
    }
}
