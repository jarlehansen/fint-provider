package no.fint.provider.events.sse;

import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;
import no.fint.event.model.Event;
import no.fint.provider.events.ProviderProps;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Synchronized
    public SseEmitter subscribe(String id, String orgId, String client) {
        FintSseEmitters fintSseEmitters = clients.get(orgId);
        if (fintSseEmitters == null) {
            fintSseEmitters = FintSseEmitters.with(providerProps.getMaxNumberOfEmitters(), this::closeEmitter);
        }

        Optional<FintSseEmitter> registeredEmitter = fintSseEmitters.get(id);
        if (registeredEmitter.isPresent()) {
            return registeredEmitter.get();
        } else {
            log.info("{}: {} connected", orgId, id);
            FintSseEmitter emitter = new FintSseEmitter(id, client,
                    TimeUnit.MINUTES.toMillis(
                            ThreadLocalRandom.current().nextInt(2000) +
                                    providerProps.getSseTimeoutMinutes()));
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

            for (FintSseEmitter emitter : toBeRemoved) {
                removeEmitter(event.getOrgId(), emitter);
            }
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
