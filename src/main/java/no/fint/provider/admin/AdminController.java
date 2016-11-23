package no.fint.provider.admin;

import no.fint.audit.plugin.mongo.MongoAuditEvent;
import no.fint.events.FintEvents;
import no.fint.provider.eventstate.EventState;
import no.fint.provider.eventstate.EventStateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/admin", produces = MediaType.APPLICATION_JSON_VALUE)
public class AdminController {

    @Autowired
    private FintEvents fintEvents;

    @Autowired
    private EventStateService eventStateService;

    @Autowired
    private MongoTemplate mongoTemplate;

    @RequestMapping(value = "/eventStates", method = RequestMethod.GET)
    public Map<String, EventState> getEventState() {
        return eventStateService.getEventStateMap();
    }

    @RequestMapping(value = "/audit/events", method = RequestMethod.GET)
    public List<MongoAuditEvent> getAllEvents() {
        return mongoTemplate.findAll(MongoAuditEvent.class);
    }

}
