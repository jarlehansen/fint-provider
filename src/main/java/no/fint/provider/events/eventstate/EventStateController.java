package no.fint.provider.events.eventstate;

import lombok.extern.slf4j.Slf4j;
import no.fint.audit.plugin.mongo.MongoAuditEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

@Slf4j
@RestController
@RequestMapping(value = "/admin", produces = MediaType.APPLICATION_JSON_VALUE)
public class EventStateController {

    @Autowired
    private EventStateService eventStateService;

    @Autowired
    private MongoTemplate mongoTemplate;


    @RequestMapping(value = "/eventStates", method = RequestMethod.GET)
    public Set<EventState> getEventState() {
        return eventStateService.getEventStates();
    }

    @RequestMapping(value = "/audit/events", method = RequestMethod.GET)
    public List<MongoAuditEvent> getAllEvents() {
        return mongoTemplate.findAll(MongoAuditEvent.class);
    }
}
