package medvedev.ilya.monitor.web.sender;

import org.springframework.web.socket.WebSocketMessage;

public interface WebSocketSender {
    void send(final WebSocketMessage message);
}
