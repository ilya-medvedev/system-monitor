package medvedev.ilya.monitor.web.sender;

import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.io.UncheckedIOException;

public class WebSocketSessionSender implements WebSocketSender {
    private final WebSocketSession session;

    public WebSocketSessionSender(final WebSocketSession session) {
        this.session = session;
    }

    @Override
    public void send(WebSocketMessage message) {
        try {
            session.sendMessage(message);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
