package no.fint.provider.events.sse;

import lombok.extern.slf4j.Slf4j;
import no.fint.event.model.Event;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.annotation.PreDestroy;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SseService {
    private static final long DEFAULT_TIMEOUT = Long.MAX_VALUE;

    private ConcurrentHashMap<String, SseClient> clients = new ConcurrentHashMap<>();

    @PreDestroy
    public void shutdown() {
        clients.values().forEach(SseClient::close);
    }

    public SseEmitter subscribe(String orgId) {
        String id = UUID.randomUUID().toString();
        SseEmitter emitter = new SseEmitter(DEFAULT_TIMEOUT);
        emitter.onCompletion(() -> {
            log.info("onCompletion called for {}, id: {}", orgId);
            clients.remove(id);
        });
        emitter.onTimeout(() -> {
            log.info("onTimeout called for {}, id: {}", orgId);
            clients.remove(id);
        });

        SseClient sseClient = new SseClient(id, orgId, emitter);
        clients.put(id, sseClient);
        return emitter;
    }

    public void send(Event event) {
        String orgId = event.getOrgId();
        List<SseClient> orgClients = this.clients.values().stream().filter(client -> client.getOrgId().equals(orgId)).collect(Collectors.toList());
        orgClients.forEach(orgClient -> {
            try {
                SseEmitter.SseEventBuilder builder = SseEmitter.event().id(event.getCorrId()).name("event").data(event);
                orgClient.getEmitter().send(builder);
            } catch (IOException | IllegalStateException e) {
                log.warn("Removing subscriber {}", orgId);
                if (orgClient.getEmitter() != null) {
                    orgClient.getEmitter().complete();
                }

                clients.remove(orgClient.getId());
            }
        });
    }

    public Map<String, Integer> getSseClients() {
        Map<String, Integer> sseClients = new HashMap<>();
        clients.values().forEach(client -> {
            String orgId = client.getOrgId();
            Integer numOfClients = sseClients.get(orgId);
            if (numOfClients == null) {
                sseClients.put(orgId, 1);
            } else {
                sseClients.put(orgId, ++numOfClients);
            }
        });
        return sseClients;
    }
}
