package medvedev.ilya.monitor.checker;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class IpCheckerConfig {
    private final boolean ipScheckerEnable;

    @Autowired
    public IpCheckerConfig(@Value("${ip.checker.enable}") final boolean ipScheckerEnable) {
        this.ipScheckerEnable = ipScheckerEnable;
    }

    public boolean isIpScheckerEnable() {
        return ipScheckerEnable;
    }
}
