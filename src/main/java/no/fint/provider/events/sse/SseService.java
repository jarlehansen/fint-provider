package no.fint.provider.events.sse;

import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;
import no.fint.event.model.Event;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.annotation.PreDestroy;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class SseService {
    private static final long DEFAULT_TIMEOUT = Long.MAX_VALUE;

    @Value("${fint.provider.max-number-of-emitters:20}")
    private int maxNumberOfEmitters;

    private ConcurrentHashMap<String, FintSseEmitters> clients = new ConcurrentHashMap<>();

    @PreDestroy
    public void shutdown() {
        clients.values().forEach(emitters -> emitters.forEach(FintSseEmitter::complete));
    }

    @Synchronized
    public SseEmitter subscribe(String id, String orgId) {
        FintSseEmitters fintSseEmitters = clients.get(orgId);
        if (fintSseEmitters == null) {
            fintSseEmitters = FintSseEmitters.with(maxNumberOfEmitters, this::closeEmitter);
        }

        FintSseEmitter emitter = new FintSseEmitter(id, DEFAULT_TIMEOUT);
        emitter.onCompletion(() -> {
            log.info("onCompletion called for {}, id: {}", orgId);
            removeEmitter(orgId, emitter);
        });
        emitter.onTimeout(() -> {
            log.info("onTimeout called for {}, id: {}", orgId);
            removeEmitter(orgId, emitter);
        });

        fintSseEmitters.add(emitter);
        clients.put(orgId, fintSseEmitters);
        return emitter;
    }

    private Void closeEmitter(SseEmitter emitter) {
        if (emitter != null) {
            emitter.complete();
        }
        return null;
    }

    private void removeEmitter(String orgId, FintSseEmitter emitter) {
        if (orgId != null && emitter != null) {
            FintSseEmitters fintSseEmitters = clients.get(orgId);
            if (fintSseEmitters != null) {
                fintSseEmitters.remove(emitter);
            }
        }
    }

    public void send(Event event) {
        clients.get(event.getOrgId()).forEach(emitter -> {
            try {
                SseEmitter.SseEventBuilder builder = SseEmitter.event().id(event.getCorrId()).name("event").data(event);
                emitter.send(builder);
            } catch (Exception e) {
                log.debug("Exception when trying to send message to SseEmitter", e);
                log.warn("Removing subscriber {}", event.getOrgId());
                removeEmitter(event.getOrgId(), emitter);
            }
        });
    }

    public ConcurrentHashMap<String, FintSseEmitters> getSseClients() {
        return clients;
    }
}
