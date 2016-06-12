package medvedev.ilya.monitor.web.handler;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import medvedev.ilya.monitor.proto.Protobuf.Message;
import medvedev.ilya.monitor.proto.Protobuf.Message.SensorInfo;
import medvedev.ilya.monitor.sensor.Sensor;
import medvedev.ilya.monitor.sensor.cpu.Cpu;
import medvedev.ilya.monitor.sensor.mem.Mem;
import medvedev.ilya.monitor.web.actor.SessionActor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
public class MonitorHandler extends AbstractWebSocketHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(MonitorHandler.class);

    private static final ActorRef NO_SENDER = ActorRef.noSender();

    private final Sensor[] sensors;

    private final ActorSystem system = ActorSystem.create();
    private final Map<WebSocketSession, ActorRef> actors = new ConcurrentHashMap<>();

    private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
    private ScheduledFuture scheduledFuture;

    @Autowired
    public MonitorHandler(
            @Value("${sensors.cpu.file}") final File cpuFile,
            @Value("${sensors.mem.file}") final File memFile
    ) {
        final Cpu cpu = Cpu.byFile(cpuFile);
        final Mem mem = Mem.byFile(memFile);

        sensors = new Sensor[] {cpu, mem};
    }

    private void sendStats() {
        final long time = System.currentTimeMillis();

        final List<SensorInfo> sensorInfoList = Arrays.stream(sensors)
                .parallel()
                .unordered()
                .map(Sensor::sensorInfo)
                .collect(Collectors.toList());

        final byte[] bytes = Message.newBuilder()
                .setTime(time)
                .addAllValue(sensorInfoList)
                .build()
                .toByteArray();

        actors.values()
                .parallelStream()
                .unordered()
                .forEach(actorRef -> {
                    final WebSocketMessage message = new BinaryMessage(bytes);

                    actorRef.tell(message, NO_SENDER);
                });
    }

    private void exceptionHandler() {
        try {
            sendStats();
        } catch (final Exception e) {
            final String message = e.getMessage();

            LOGGER.warn(message, e);
        }
    }

    @Override
    public synchronized void afterConnectionEstablished(final WebSocketSession session) {
        if (actors.isEmpty()) {
            scheduledFuture = executorService.scheduleAtFixedRate(this::exceptionHandler, 0, 1, TimeUnit.SECONDS);
        }

        final ActorRef actorRef = system.actorOf(Props.create(SessionActor.class, session));

        actors.put(session, actorRef);
    }

    @Override
    public synchronized void afterConnectionClosed(final WebSocketSession session, final CloseStatus status) {
        final ActorRef actorRef = actors.remove(session);

        system.stop(actorRef);

        if (actors.isEmpty()) {
            scheduledFuture.cancel(true);

            Arrays.stream(sensors)
                    .parallel()
                    .unordered()
                    .forEach(Sensor::clear);
        }
    }
}
