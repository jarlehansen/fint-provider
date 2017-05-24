package no.fint.provider.admin;

import lombok.extern.slf4j.Slf4j;
import no.fint.audit.plugin.mongo.MongoAuditEvent;
import no.fint.provider.events.sse.FintSseEmitters;
import no.fint.provider.events.sse.SseService;
import no.fint.provider.eventstate.EventState;
import no.fint.provider.eventstate.EventStateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@RestController
@RequestMapping(value = "/admin", produces = MediaType.APPLICATION_JSON_VALUE)
public class AdminController {

    @Autowired
    private EventStateService eventStateService;

    @Autowired
    private SseService sseService;

    @Autowired
    private MongoTemplate mongoTemplate;

    @RequestMapping(value = "/sse-clients", method = RequestMethod.GET)
    public List<SseOrg> getSseClients() {
        Map<String, FintSseEmitters> clients = sseService.getSseClients();
        log.info("Connected SSE clients: {}", clients);

        List<SseOrg> orgs = new ArrayList<>();
        clients.forEach((key, value) -> {
            List<SseClient> sseClients = new ArrayList<>();
            value.forEach(emitter -> sseClients.add(new SseClient(emitter.getRegistered(), emitter.getId())));

            orgs.add(new SseOrg(key, sseClients));
        });
        return orgs;
    }

    @RequestMapping(value = "/sse-clients", method = RequestMethod.DELETE)
    public void removeSseClients() {
        sseService.removeAll();
    }

    @RequestMapping(value = "/eventStates", method = RequestMethod.GET)
    public Set<EventState> getEventState() {
        return eventStateService.getEventStates();
    }

    @RequestMapping(value = "/audit/events", method = RequestMethod.GET)
    public List<MongoAuditEvent> getAllEvents() {
        return mongoTemplate.findAll(MongoAuditEvent.class);
    }

}
