package medvedev.ilya.monitor.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import java.io.File;

@Validated
@ConfigurationProperties(prefix = "sensor")
public class SensorProperties {
    @NotNull
    private CpuProperties cpu;

    @NotNull
    private MemProperties mem;

    @NotNull
    private DiskProperties disk;

    @NotNull
    private NetProperties net;

    public CpuProperties getCpu() {
        return cpu;
    }

    public void setCpu(final CpuProperties cpu) {
        this.cpu = cpu;
    }

    public MemProperties getMem() {
        return mem;
    }

    public void setMem(final MemProperties mem) {
        this.mem = mem;
    }

    public DiskProperties getDisk() {
        return disk;
    }

    public void setDisk(final DiskProperties disk) {
        this.disk = disk;
    }

    public NetProperties getNet() {
        return net;
    }

    public void setNet(final NetProperties net) {
        this.net = net;
    }

    @Validated
    public static class CpuProperties {
        @NotNull
        private File file;

        public File getFile() {
            return file;
        }

        public void setFile(final File file) {
            this.file = file;
        }
    }

    @Validated
    public static class MemProperties {
        @NotNull
        private File file;

        public File getFile() {
            return file;
        }

        public void setFile(final File file) {
            this.file = file;
        }
    }

    @Validated
    public static class DiskProperties {
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

    @Validated
    public static class NetProperties {
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
}
