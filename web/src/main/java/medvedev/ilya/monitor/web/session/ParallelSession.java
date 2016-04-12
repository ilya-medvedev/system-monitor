package medvedev.ilya.monitor.web.session;

import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

public class ParallelSession {
    private final WebSocketSession session;

    public ParallelSession(final WebSocketSession session) {
        this.session = session;
    }

    public synchronized void send(final WebSocketMessage<?> message) {
        try {
            session.sendMessage(message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
