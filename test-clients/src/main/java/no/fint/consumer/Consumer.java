package no.fint.consumer;

import lombok.extern.slf4j.Slf4j;
import no.fint.Actions;
import no.fint.Constants;
import no.fint.adapter.Adapter;
import no.fint.event.model.DefaultActions;
import no.fint.event.model.Event;
import no.fint.event.model.HeaderConstants;
import no.fint.events.FintEvents;
import no.fint.events.FintEventsHealth;
import no.fint.events.annotations.FintEventListener;
import no.fint.events.queue.QueueType;
import no.fint.model.relation.FintResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/consumer")
public class Consumer {

    @Autowired
    private FintEvents fintEvents;

    @Autowired
    private FintEventsHealth fintEventsHealth;

    @Autowired
    private Adapter adapter;

    @GetMapping("/reconnect")
    public void reconnect() {
        fintEvents.reconnect();
    }

    @GetMapping("/healthCheck")
    public Event healthCheck(@RequestHeader(value = HeaderConstants.ORG_ID, defaultValue = Constants.ORGID) String orgId,
                             @RequestHeader(value = HeaderConstants.CLIENT, defaultValue = Constants.CLIENT) String client) {
        Event<String> health = new Event<>(orgId, Constants.SOURCE, DefaultActions.HEALTH, client);
        return fintEventsHealth.sendHealthCheck(orgId, health.getCorrId(), health);
    }

    @PostMapping("/orgIds/{orgId}")
    public void registerOrgId(@RequestHeader(value = HeaderConstants.ORG_ID) String orgId) {
        Event event = new Event(orgId, Constants.SOURCE, DefaultActions.REGISTER_ORG_ID.name(), Constants.CLIENT);
        fintEvents.sendDownstream("system", event);
        adapter.registerOrgId(orgId);
    }

    @FintEventListener(type = QueueType.UPSTREAM)
    public void receive(Event<FintResource> event) {
        log.info("Upstream event: {}", event);
        fintEvents.getTempQueue("test-consumer-" + event.getCorrId()).offer(event);
    }
}
