package medvedev.ilya.monitor.web.sender.session;

import medvedev.ilya.monitor.web.sender.WebSocketSender;
import medvedev.ilya.monitor.web.sender.WedSocketSenderException;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

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
            throw new WedSocketSenderException(e);
        }
    }
}
