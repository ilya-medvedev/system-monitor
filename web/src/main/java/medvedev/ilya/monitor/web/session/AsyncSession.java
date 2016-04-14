package medvedev.ilya.monitor.web.session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class AsyncSession {
    private static final Logger LOGGER = LoggerFactory.getLogger(AsyncSession.class);

    private final Executor sender = Executors.newSingleThreadExecutor();

    private final WebSocketSession session;

    public AsyncSession(final WebSocketSession session) {
        this.session = session;
    }

    public void send(final byte[] bytes) {
        final WebSocketMessage message = new BinaryMessage(bytes);

        sender.execute(() -> {
            try {
                session.sendMessage(message);
            } catch (final Exception e) {
                LOGGER.warn("{}", message, e);
            }
        });
    }
}
