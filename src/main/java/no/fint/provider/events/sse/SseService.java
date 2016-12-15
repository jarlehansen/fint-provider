package no.fint.provider.events.sse;

import com.google.common.collect.EvictingQueue;
import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;
import no.fint.event.model.Event;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.annotation.PreDestroy;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class SseService {
    private static final int MAX_NUMBER_OF_EMITTERS = 20;
    private static final long DEFAULT_TIMEOUT = Long.MAX_VALUE;

    private ConcurrentHashMap<String, EvictingQueue<SseEmitter>> clients = new ConcurrentHashMap<>();

    @PreDestroy
    public void shutdown() {
        clients.values().forEach(emitters -> emitters.forEach(SseEmitter::complete));
    }

    @Synchronized
    public SseEmitter subscribe(String orgId) {
        EvictingQueue<SseEmitter> sseEmitters = clients.get(orgId);
        if (sseEmitters == null) {
            sseEmitters = EvictingQueue.create(MAX_NUMBER_OF_EMITTERS);
        }

        SseEmitter emitter = new SseEmitter(DEFAULT_TIMEOUT);
        emitter.onCompletion(() -> {
            log.info("onCompletion called for {}, id: {}", orgId);
            removeEmitter(orgId, emitter);
        });
        emitter.onTimeout(() -> {
            log.info("onTimeout called for {}, id: {}", orgId);
            removeEmitter(orgId, emitter);
        });

        sseEmitters.add(emitter);
        clients.put(orgId, sseEmitters);
        return emitter;
    }

    private void removeEmitter(String orgId, SseEmitter emitter) {
        if (orgId != null && emitter != null) {
            EvictingQueue<SseEmitter> emitters = clients.get(orgId);
            if (emitters != null) {
                emitters.remove(emitter);
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

    public ConcurrentHashMap<String, EvictingQueue<SseEmitter>> getSseClients() {
        return clients;
    }
}
