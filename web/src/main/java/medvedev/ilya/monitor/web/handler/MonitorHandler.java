package medvedev.ilya.monitor.web.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import medvedev.ilya.monitor.sensor.Sensor;
import medvedev.ilya.monitor.sensor.cpu.Cpu;
import medvedev.ilya.monitor.sensor.mem.Mem;
import medvedev.ilya.monitor.util.ExceptionHandler;
import medvedev.ilya.monitor.web.session.ParallelSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class MonitorHandler extends AbstractWebSocketHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(MonitorHandler.class);
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final Sensor[] sensors;

    private final Map<WebSocketSession, ParallelSession> sessions = new ConcurrentHashMap<>();

    private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
    private final Runnable runnable;
    private ScheduledFuture scheduledFuture;

    public MonitorHandler() {
        final Cpu cpu = Cpu.byFile();
        final Mem mem = Mem.byFile();

        sensors = new Sensor[] {cpu, mem};

        final Executor sender = Executors.newCachedThreadPool();
        final Runnable sendStats = () -> sender.execute(this::sendStats);

        runnable = ExceptionHandler.runnableHandler(sendStats, LOGGER);
    }

    @Override
    public synchronized void afterConnectionEstablished(final WebSocketSession session) {
        if (sessions.isEmpty()) {
            scheduledFuture = executorService.scheduleAtFixedRate(runnable, 0, 1, TimeUnit.SECONDS);
        }

        final ParallelSession parallelSession = new ParallelSession(session);

        sessions.put(session, parallelSession);
    }

    private void sendStats() {
        Arrays.stream(sensors)
                .parallel()
                .unordered()
                .flatMap(Sensor::sensorValue)
                .map(sensorValue -> {
                    try {
                        return MAPPER.writeValueAsString(sensorValue);
                    } catch (final JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                })
                .map(TextMessage::new)
                .forEach(message -> sessions.values()
                        .parallelStream()
                        .unordered()
                        .forEach(session -> {
                            try {
                                session.send(message);
                            } catch (final Exception e) {
                                LOGGER.warn("{}", session, e);
                            }
                        }));
    }

    @Override
    public synchronized void afterConnectionClosed(final WebSocketSession session, final CloseStatus status) {
        sessions.remove(session);

        if (sessions.isEmpty()) {
            scheduledFuture.cancel(true);

            Arrays.stream(sensors)
                    .parallel()
                    .unordered()
                    .forEach(Sensor::clear);
        }
    }
}
