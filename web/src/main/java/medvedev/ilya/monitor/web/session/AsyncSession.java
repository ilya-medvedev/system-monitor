package medvedev.ilya.monitor.web.session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class AsyncSession {
    private static final Logger LOGGER = LoggerFactory.getLogger(AsyncSession.class);

    private final Executor sender = Executors.newSingleThreadExecutor();

    private final WebSocketSession session;

    public AsyncSession(final WebSocketSession session) {
        this.session = session;
    }

    public void send(final WebSocketMessage<?> message) {
        sender.execute(() -> exceptionHandler(message));
    }

    private void exceptionHandler(final WebSocketMessage<?> message) {
        try {
            session.sendMessage(message);
        } catch (IOException e) {
            LOGGER.warn("{}", message, e);
        }
    }
}
