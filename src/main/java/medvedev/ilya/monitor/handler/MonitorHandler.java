package medvedev.ilya.monitor.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import medvedev.ilya.monitor.model.Message;
import medvedev.ilya.monitor.sensor.cpu.Cpu;
import medvedev.ilya.monitor.sensor.mem.Mem;
import medvedev.ilya.monitor.util.ExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class MonitorHandler extends AbstractWebSocketHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(MonitorHandler.class);
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final Cpu cpu = new Cpu();
    private final Mem mem = new Mem();

    private final Set<WebSocketSession> sessions = new CopyOnWriteArraySet<>();
    private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

    private ScheduledFuture scheduledFuture;

    @Override
    public synchronized void afterConnectionEstablished(final WebSocketSession session) {
        if (sessions.isEmpty()) {
            scheduledFuture = executorService.scheduleAtFixedRate(ExceptionHandler.runnableHandler(() -> {
                final Message message = new Message(cpu, mem);

                final String string;
                try {
                    string = MAPPER.writeValueAsString(message);
                } catch (final JsonProcessingException e) {
                    throw new RuntimeException(e);
                }

                final WebSocketMessage socketMessage = new TextMessage(string);

                sessions.parallelStream()
                        .unordered()
                        .forEach(s -> {
                            try {
                                s.sendMessage(socketMessage);
                            } catch (final Exception e) {
                                LOGGER.warn("{}", s, e);
                            }
                        });
            }, LOGGER), 0, 1, TimeUnit.SECONDS);
        }

        sessions.add(session);
    }

    @Override
    public synchronized void afterConnectionClosed(final WebSocketSession session, final CloseStatus status) {
        sessions.remove(session);

        if (sessions.isEmpty()) {
            scheduledFuture.cancel(true);
            cpu.clear();
        }
    }
}
