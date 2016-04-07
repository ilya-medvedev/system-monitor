package medvedev.ilya.monitor.checker;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;

@Component
class IpService {
    private final URL url;

    @Autowired
    IpService(@Value("${ip.service.url}") final String url) {
        try {
            this.url = new URL(url);
        } catch (final MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    String currentIp() {
        try (
                final InputStream inputStream = url.openConnection()
                        .getInputStream();
                final Reader reader = new InputStreamReader(inputStream);
                final BufferedReader in = new BufferedReader(reader)
        ) {
            return in.readLine();
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }
}
