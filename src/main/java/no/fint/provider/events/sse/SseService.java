package no.fint.provider.events.sse;

import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;
import no.fint.event.model.Event;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.annotation.PreDestroy;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Slf4j
@RefreshScope
@Service
public class SseService {
    private static final long DEFAULT_TIMEOUT = TimeUnit.MILLISECONDS.convert(1, TimeUnit.DAYS);

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

        Optional<FintSseEmitter> registeredEmitter = fintSseEmitters.get(id);
        if (registeredEmitter.isPresent()) {
            return registeredEmitter.get();
        } else {
            FintSseEmitter emitter = new FintSseEmitter(id, DEFAULT_TIMEOUT);
            emitter.onCompletion(() -> {
                log.info("onCompletion called for {}, id: {}", orgId, emitter.getId());
                removeEmitter(orgId, emitter);
            });
            emitter.onTimeout(() -> {
                log.info("onTimeout called for {}, id: {}", orgId, emitter.getId());
                removeEmitter(orgId, emitter);
            });

            fintSseEmitters.add(emitter);
            clients.put(orgId, fintSseEmitters);
            return emitter;
        }
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
        List<FintSseEmitter> toBeRemoved = new ArrayList<>();
        clients.get(event.getOrgId()).forEach(emitter -> {
            try {
                SseEmitter.SseEventBuilder builder = SseEmitter.event().id(event.getCorrId()).name("event").data(event);
                emitter.send(builder);
            } catch (Exception e) {
                log.debug("Exception when trying to send message to SseEmitter", e);
                log.warn("Removing subscriber {}", event.getOrgId());
                toBeRemoved.add(emitter);
            }
        });

        for (FintSseEmitter emitter : toBeRemoved) {
            removeEmitter(event.getOrgId(), emitter);
        }
    }

    public Map<String, FintSseEmitters> getSseClients() {
        return new HashMap<>(clients);
    }

    public void removeAll() {
        clients.values().forEach(emitters -> emitters.forEach(FintSseEmitter::complete));
        clients.clear();
    }
}
