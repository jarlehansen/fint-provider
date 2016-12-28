package no.fint.provider.admin;

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

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
    public ConcurrentHashMap<String, FintSseEmitters> getSseClients() {
        return sseService.getSseClients();
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
