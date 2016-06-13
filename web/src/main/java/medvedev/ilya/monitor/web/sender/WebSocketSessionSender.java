package medvedev.ilya.monitor.web.sender;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

public class WebSocketSessionSender implements WebSocketSender {
    private static final Logger LOGGER = LoggerFactory.getLogger(WebSocketSessionSender.class);

    private static final String DELIVERY_ERROR = "Message {} was not delivered to {}";

    private final WebSocketSession session;

    public WebSocketSessionSender(final WebSocketSession session) {
        this.session = session;
    }

    @Override
    public void send(WebSocketMessage message) {
        try {
            session.sendMessage(message);
        } catch (IOException e) {
            LOGGER.warn(DELIVERY_ERROR, message, session, e);
        }
    }
}
