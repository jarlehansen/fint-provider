package no.fint.provider.events.sse;

import lombok.extern.slf4j.Slf4j;
import no.fint.event.model.Event;
import no.fint.provider.events.ProviderProps;
import org.jooq.lambda.function.Consumer2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.annotation.PreDestroy;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class SseService {
    @Autowired
    private ProviderProps providerProps;

    private ConcurrentHashMap<String, FintSseEmitters> clients = new ConcurrentHashMap<>();

    @PreDestroy
    public void shutdown() {
        clients.values().forEach(emitters -> emitters.forEach(FintSseEmitter::complete));
    }

    public synchronized SseEmitter subscribe(String id, String orgId, String client) {
        final FintSseEmitters fintSseEmitters = Optional
                .ofNullable(clients.get(orgId))
                .orElseGet(() -> FintSseEmitters.with(providerProps.getMaxNumberOfEmitters(), SseEmitter::complete));

        return fintSseEmitters.get(id).orElseGet(() -> {
            log.info("{}: {} connected", orgId, id);
            FintSseEmitter emitter = new FintSseEmitter(id, client,
                    TimeUnit.MINUTES.toMillis(
                            ThreadLocalRandom.current().nextInt(2000) +
                                    providerProps.getSseTimeoutMinutes()));

            emitter.onCompletion(Consumer2.from(this::removeEmitter).acceptPartially(orgId, emitter));

            fintSseEmitters.add(emitter);
            clients.put(orgId, fintSseEmitters);
            return emitter;
        });
    }

    private void removeEmitter(String orgId, FintSseEmitter emitter) {
        if (orgId != null && emitter != null) {
            FintSseEmitters fintSseEmitters = clients.get(orgId);
            if (fintSseEmitters != null) {
                log.info("Removing emitter {} for {}", emitter.getId(), orgId);
                fintSseEmitters.remove(emitter);
            }
        }
    }

    public void send(Event event) {
        FintSseEmitters emitters = clients.get(event.getOrgId());
        if (emitters == null) {
            log.info("No sse clients registered for {}", event.getOrgId());
        } else {
            emitters.forEach(emitter -> {
                try {
                    SseEmitter.SseEventBuilder builder = SseEmitter.event().id(event.getCorrId()).name(event.getAction()).data(event).reconnectTime(5000L);
                    emitter.send(builder);
                } catch (Exception e) {
                    log.info("Error sending message to SseEmitter {} {}: {}", emitter.getClient(), emitter.getId(), e.getMessage());
                    log.debug("Details: {}", event, e);
                }
            });

        }
    }

    @Scheduled(initialDelay = 15000, fixedRateString = "${fint.provider.sse.heartbeat:15000}")
    public void sendHeartbeat() {
        log.debug("Heartbeat");
        clients.forEach((orgId, emitters) -> emitters.forEach(emitter -> {
            try {
                SseEmitter.SseEventBuilder builder = SseEmitter.event().comment("Heartbeat").reconnectTime(5000L);
                emitter.send(builder);
            } catch (Exception e) {
                log.info("Error sending message to SseEmitter {} {}: {}", emitter.getClient(), emitter.getId(), e.getMessage());
                log.debug("Details:", e);
            }
        }));

    }

    public Map<String, FintSseEmitters> getSseClients() {
        return new HashMap<>(clients);
    }

    public void removeAll() {
        clients.values().forEach(emitters -> emitters.forEach(FintSseEmitter::complete));
        clients.clear();
    }
}
