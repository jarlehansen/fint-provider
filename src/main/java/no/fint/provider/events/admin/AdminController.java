package no.fint.provider.events.admin;

import lombok.extern.slf4j.Slf4j;
import no.fint.audit.plugin.mongo.MongoAuditEvent;
import no.fint.events.FintEvents;
import no.fint.provider.events.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@Slf4j
@RestController
@RequestMapping(value = "/admin", produces = MediaType.APPLICATION_JSON_VALUE)
public class AdminController {

    @Autowired
    private FintEvents fintEvents;

    @Autowired
    private MongoTemplate mongoTemplate;

    @GetMapping("/audit/events")
    public List<MongoAuditEvent> getAllEvents() {
        return mongoTemplate.findAll(MongoAuditEvent.class);
    }

    @DeleteMapping("/tempQueues")
    public boolean deleteTempQueues() {
        return fintEvents.deleteTempQueues();
    }

    @DeleteMapping("/clear/all")
    public void clearAll() {
        Set<String> queues = fintEvents.getQueues();
        for (String queue : queues) {
            fintEvents.getQueue(queue).clear();
        }
    }

    @DeleteMapping("/clear/downstream")
    public void clearDownstream(@RequestHeader(Constants.HEADER_ORGID) String orgId) {
        fintEvents.getDownstream(orgId).clear();
    }

    @DeleteMapping("/clear/upstream")
    public void clearUpstream(@RequestHeader(Constants.HEADER_ORGID) String orgId) {
        fintEvents.getUpstream(orgId).clear();
    }
}
