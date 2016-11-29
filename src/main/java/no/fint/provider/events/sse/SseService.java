package no.fint.provider.events.sse;

import lombok.extern.slf4j.Slf4j;
import no.fint.event.model.Event;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.annotation.PreDestroy;
import java.io.IOException;
import java.util.*;

@Slf4j
@Service
public class SseService {
    private static final long DEFAULT_TIMEOUT = Long.MAX_VALUE;

    private final Map<String, SseEmitter> emitters = new HashMap<>();

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
        List<String> toBeRemoved = new ArrayList<>();
        emitters.keySet().stream().filter(orgId -> (event.getOrgId().equals(orgId))).forEach(orgId -> {
            try {
                SseEmitter.SseEventBuilder builder = SseEmitter.event().id(event.getCorrId()).name("event").data(event);
                emitters.get(orgId).send(builder);
            } catch (IOException | IllegalStateException e) {
                toBeRemoved.add(orgId);
                log.warn("Exception when trying to send SSE message, removing subscriber {}", orgId);
            }
        });

        toBeRemoved.forEach(emitters::remove);
    }

    Optional<SseEmitter> getSseEmitter(String orgId) {
        return Optional.ofNullable(emitters.get(orgId));
    }

    public Set<String> getSseClients() {
        return emitters.keySet();
    }
}
