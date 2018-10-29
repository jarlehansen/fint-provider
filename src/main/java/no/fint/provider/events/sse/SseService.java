package no.fint.provider.events.sse;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import lombok.extern.slf4j.Slf4j;
import no.fint.event.model.Event;
import no.fint.provider.events.ProviderProps;
import org.jooq.lambda.function.Consumer2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.annotation.PreDestroy;
import java.util.*;
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

            emitter.onCompletion(Consumer2.from(this::removeEmitter).acceptPartially(orgId,emitter));
            emitter.onTimeout(Consumer2.from(this::removeEmitter).acceptPartially(orgId,emitter));

            fintSseEmitters.add(emitter);
            clients.put(orgId, fintSseEmitters);
            return emitter;
        });
    }

    private void removeEmitter(String orgId, FintSseEmitter emitter) {
        if (orgId != null && emitter != null) {
            FintSseEmitters fintSseEmitters = clients.get(orgId);
            if (fintSseEmitters != null) {
                emitter.complete();
                fintSseEmitters.remove(emitter);
            }
        }
    }

    public void send(Event event) {
        FintSseEmitters emitters = clients.get(event.getOrgId());
        if (emitters == null) {
            log.info("No sse clients registered for {}", event.getOrgId());
        } else {
            List<FintSseEmitter> toBeRemoved = new ArrayList<>();
            emitters.forEach(emitter -> {
                try {
                    SseEmitter.SseEventBuilder builder = SseEmitter.event().id(event.getCorrId()).name(event.getAction()).data(event).reconnectTime(5000L);
                    emitter.send(builder);
                } catch (Exception e) {
                    log.warn("Exception when trying to send message to SseEmitter", e.getMessage());
                    log.warn("Removing subscriber {}", event.getOrgId());
                    log.debug("Details: {}", event, e);
                    toBeRemoved.add(emitter);
                }
            });

            toBeRemoved.forEach(Consumer2.from(this::removeEmitter).acceptPartially(event.getOrgId()));
        }
    }

    @Scheduled(initialDelay = 15000, fixedRateString = "${fint.provider.sse.heartbeat:15000}")
    public void sendHeartbeat() {
        log.debug("Heartbeat");
        Multimap<String, FintSseEmitter> toBeRemoved = MultimapBuilder.hashKeys().linkedListValues().build();
        clients.forEach((orgId, emitters) -> {
            emitters.forEach(emitter -> {
                try {
                    SseEmitter.SseEventBuilder builder = SseEmitter.event().comment("Heartbeat").reconnectTime(5000L);
                    emitter.send(builder);
                } catch (Exception e) {
                    log.warn("Exception when trying to send message to SseEmitter: {}", e.getMessage());
                    log.warn("Removing emitter {} for {}", emitter.getId(), orgId);
                    toBeRemoved.put(orgId, emitter);
                }
            });
        });

        toBeRemoved.forEach(this::removeEmitter);
    }

    public Map<String, FintSseEmitters> getSseClients() {
        return new HashMap<>(clients);
    }

    public void removeAll() {
        clients.values().forEach(emitters -> emitters.forEach(FintSseEmitter::complete));
        clients.clear();
    }
}
