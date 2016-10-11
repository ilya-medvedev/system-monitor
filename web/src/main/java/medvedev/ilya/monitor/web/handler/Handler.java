package medvedev.ilya.monitor.web.handler;

import akka.actor.TypedActorFactory;
import akka.actor.TypedProps;
import akka.japi.Creator;
import medvedev.ilya.monitor.protobuf.SensorMessage;
import medvedev.ilya.monitor.protobuf.SensorMessage.SensorInfo;
import medvedev.ilya.monitor.sensor.Sensor;
import medvedev.ilya.monitor.web.sender.WebSocketSender;
import medvedev.ilya.monitor.web.sender.session.WebSocketSessionSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

import java.io.Closeable;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class Handler extends AbstractWebSocketHandler implements Closeable {
    private static final Logger LOGGER = LoggerFactory.getLogger(Handler.class);

    private final TypedActorFactory typedActorFactory;
    private final Sensor[] sensors;
    private final byte period;

    private final Map<WebSocketSession, WebSocketSender> sessions = new ConcurrentHashMap<>();

    private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
    private ScheduledFuture scheduledFuture;

    public Handler(final TypedActorFactory typedActorFactory, final Sensor[] sensors, final byte period) {
        this.typedActorFactory = typedActorFactory;
        this.sensors = sensors;
        this.period = period;
    }

    private void sendStats() {
        final long time = System.currentTimeMillis();

        final List<SensorInfo> sensorInfoList = Arrays.stream(sensors)
                .parallel()
                .unordered()
                .map(Sensor::sensorInfo)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        final byte[] bytes = SensorMessage.newBuilder()
                .setTime(time)
                .addAllValue(sensorInfoList)
                .build()
                .toByteArray();

        sessions.values()
                .parallelStream()
                .unordered()
                .forEach(webSocketSender -> {
                    final WebSocketMessage message = new BinaryMessage(bytes);

                    webSocketSender.send(message);
                });
    }

    private void exceptionHandler() {
        try {
            sendStats();
        } catch (final Exception e) {
            LOGGER.warn("Unknown error.", e);
        }
    }

    private synchronized void putWebSocketSender(
            final WebSocketSession session,
            final WebSocketSender webSocketSender
    ) {
        final boolean empty = sessions.isEmpty();

        sessions.put(session, webSocketSender);

        if (empty) {
            scheduledFuture = executorService.scheduleAtFixedRate(this::exceptionHandler, 0, period, TimeUnit.SECONDS);
        }
    }

    private synchronized WebSocketSender removeWebSocketSender(final WebSocketSession session) {
        final WebSocketSender webSocketSender = sessions.remove(session);
        final boolean empty = sessions.isEmpty();

        if (empty) {
            scheduledFuture.cancel(true);

            Arrays.stream(sensors)
                    .parallel()
                    .unordered()
                    .forEach(Sensor::clear);
        }

        return webSocketSender;
    }

    @Override
    public void afterConnectionEstablished(final WebSocketSession session) {
        final Creator<WebSocketSender> creator = () -> new WebSocketSessionSender(session);
        final TypedProps<WebSocketSender> typedProps = new TypedProps<>(WebSocketSender.class, creator);

        final WebSocketSender webSocketSender = typedActorFactory.typedActorOf(typedProps);

        putWebSocketSender(session, webSocketSender);
    }

    @Override
    public void afterConnectionClosed(final WebSocketSession session, final CloseStatus status) {
        final WebSocketSender webSocketSender = removeWebSocketSender(session);

        typedActorFactory.stop(webSocketSender);
    }

    @Override
    public void close() throws IOException {
        executorService.shutdown();
    }
}
