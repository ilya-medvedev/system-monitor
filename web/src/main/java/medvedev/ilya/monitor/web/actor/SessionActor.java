package medvedev.ilya.monitor.web.actor;

import akka.actor.UntypedActor;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

public class SessionActor extends UntypedActor {
    private final WebSocketSession session;

    public SessionActor(final WebSocketSession session) {
        this.session = session;
    }

    @Override
    public void onReceive(final Object message) throws Exception {
        session.sendMessage((WebSocketMessage) message);
    }
}
