package no.fint.consumer;

import lombok.extern.slf4j.Slf4j;
import no.fint.Actions;
import no.fint.adapter.Adapter;
import no.fint.Constants;
import no.fint.event.model.Event;
import no.fint.events.FintEvents;
import no.fint.events.FintEventsHealth;
import no.fint.events.HealthCheck;
import no.fint.events.annotations.FintEventListener;
import no.fint.model.relation.FintResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;

@Slf4j
@RestController
@RequestMapping("/consumer")
public class Consumer {

    @Autowired
    private Adapter adapter;

    @Autowired
    private FintEvents fintEvents;

    @Autowired
    private FintEventsHealth fintEventsHealth;
    private HealthCheck<Event> healthCheck;

    @PostConstruct
    public void init() {
        createRedissonClient();
    }

    public void createRedissonClient() {
        fintEvents.registerUpstreamListener(Consumer.class, Constants.ORGID);
        healthCheck = fintEventsHealth.registerClient();
    }

    @GetMapping("/reconnect")
    public void reconnect() {
        fintEvents.reconnect();
        fintEventsHealth.deregisterClient();
        createRedissonClient();

        adapter.shutdown();
        adapter.init();
    }

    @GetMapping("/healthCheck")
    public Event healthCheck(@RequestHeader(value = Constants.HEADER_ORGID, defaultValue = Constants.ORGID) String orgId,
                             @RequestHeader(value = Constants.HEADER_CLIENT, defaultValue = Constants.CLIENT) String client) {
        Event<String> health = new Event<>(orgId, Constants.SOURCE, Actions.HEALTH, client);
        return healthCheck.check(health);
    }

    @FintEventListener
    public void receive(Event<FintResource> event) {
        log.info("Upstream event: {}", event);
        fintEvents.getTempQueue("test-consumer-" + event.getCorrId()).offer(event);
    }
}
