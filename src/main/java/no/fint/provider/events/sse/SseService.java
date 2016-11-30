package no.fint.provider.events.sse;

import lombok.extern.slf4j.Slf4j;
import no.fint.event.model.Event;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.annotation.PreDestroy;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class SseService {
    private static final long DEFAULT_TIMEOUT = Long.MAX_VALUE;

    private final ConcurrentHashMap<String, SseEmitter> emitters = new ConcurrentHashMap<>();

    @PreDestroy
    public void shutdown() {
        emitters.values().forEach(ResponseBodyEmitter::complete);
    }

    public boolean newListener(String orgId) {
        return !(emitters.containsKey(orgId));
    }

    public SseEmitter subscribe(String orgId) {
        if (emitters.containsKey(orgId)) {
            SseEmitter emitter = emitters.get(orgId);
            emitter.complete();
            emitters.remove(orgId);
        }

        SseEmitter emitter = new SseEmitter(DEFAULT_TIMEOUT);
        emitter.onCompletion(() -> {
            log.info("onCompletion called for {}", orgId);
            emitters.remove(orgId);
        });
        emitter.onTimeout(() -> {
            log.info("onTimeout called for {}", orgId);
            emitters.remove(orgId);
        });
        emitters.put(orgId, emitter);
        return emitter;
    }

    public void send(Event event) {
        String orgId = event.getOrgId();
        SseEmitter emitter = emitters.get(orgId);
        if (emitter != null) {
            try {
                SseEmitter.SseEventBuilder builder = SseEmitter.event().id(event.getCorrId()).name("event").data(event);
                emitter.send(builder);
            } catch (IOException | IllegalStateException e) {
                removeEmitter(orgId);
            }
        }
    }

    @Scheduled(fixedDelayString = "3000")
    public void ping() {
        List<String> toBeRemoved = new ArrayList<>();
        SseEmitter.SseEventBuilder builder = SseEmitter.event().id(UUID.randomUUID().toString()).name("ping");
        for (Map.Entry<String, SseEmitter> entry : emitters.entrySet()) {
            String orgId = entry.getKey();
            try {
                SseEmitter emitter = emitters.get(orgId);
                if (emitter == null) {
                    toBeRemoved.add(orgId);
                } else {
                    emitter.send(builder);
                }
            } catch (IllegalStateException | IOException e) {
                toBeRemoved.add(orgId);
            }
        }

        toBeRemoved.forEach(this::removeEmitter);
    }

    private void removeEmitter(String orgId) {
        log.warn("Removing subscriber {}", orgId);
        SseEmitter emitter = emitters.get(orgId);
        if (emitter != null) {
            emitter.complete();
        }
        emitters.remove(orgId);
    }

    Optional<SseEmitter> getSseEmitter(String orgId) {
        return Optional.ofNullable(emitters.get(orgId));
    }

    public Set<String> getSseClients() {
        return emitters.keySet();
    }
}
