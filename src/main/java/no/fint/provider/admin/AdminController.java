package no.fint.provider.admin;

import lombok.extern.slf4j.Slf4j;
import no.fint.audit.plugin.mongo.MongoAuditEvent;
import no.fint.provider.events.sse.FintSseEmitters;
import no.fint.provider.events.sse.SseService;
import no.fint.provider.eventstate.EventState;
import no.fint.provider.eventstate.EventStateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping(value = "/provider/admin", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
public class AdminController {

    @Autowired
    private EventStateRepository eventStateRepository;

    @Autowired
    private SseService sseService;

    @Autowired
    private MongoTemplate mongoTemplate;

    @RequestMapping("/sse-clients")
    public List<SseOrg> getSseClients() {
        Map<String, FintSseEmitters> clients = new HashMap<>();
        clients.putAll(sseService.getSseClients());
        log.info("Connected SSE clients: {}", clients);

        List<SseOrg> orgs = new ArrayList<>();
        clients.entrySet().forEach(client -> {
            List<SseClient> sseClients = new ArrayList<>();
            client.getValue().forEach(emitter -> sseClients.add(new SseClient(emitter.getRegistered(), emitter.getId())));

            orgs.add(new SseOrg(client.getKey(), sseClients));
        });
        return orgs;
    }

    @RequestMapping("/eventStates")
    public Map<String, EventState> getEventState() {
        return eventStateRepository.getMap();
    }

    @RequestMapping("/audit/events")
    public List<MongoAuditEvent> getAllEvents() {
        return mongoTemplate.findAll(MongoAuditEvent.class);
    }

}
