package no.fint;

import lombok.extern.slf4j.Slf4j;
import no.fint.dto.Value;
import no.fint.event.model.Event;
import no.fint.events.FintEvents;
import no.fint.events.FintEventsHealth;
import no.fint.events.HealthCheck;
import no.fint.events.annotations.FintEventListener;
import org.redisson.api.RBlockingQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
@RequestMapping("/consumer")
public class Consumer {

    @Autowired
    private FintEvents fintEvents;

    @Autowired
    private FintEventsHealth fintEventsHealth;
    private HealthCheck<Event> healthCheck;

    @PostConstruct
    public void init() {
        fintEvents.registerUpstreamListener(Consumer.class, Constants.ORGID);
        healthCheck = fintEventsHealth.registerClient();
    }

    @GetMapping("/healthCheck")
    public Event healthCheck(@RequestHeader(value = Constants.HEADER_ORGID, defaultValue = Constants.ORGID) String orgId,
                             @RequestHeader(value = Constants.HEADER_CLIENT, defaultValue = Constants.CLIENT) String client) {
        Event<String> health = new Event<>(orgId, Constants.SOURCE, Actions.HEALTH, client);
        return healthCheck.check(health);
    }

    @GetMapping("/values")
    public List<Value> getAllValues(@RequestHeader(value = Constants.HEADER_ORGID, defaultValue = Constants.ORGID) String orgId,
                                    @RequestHeader(value = Constants.HEADER_CLIENT, defaultValue = Constants.CLIENT) String client) throws InterruptedException {
        Event<Value> event = new Event<>(orgId, Constants.SOURCE, Actions.GET_ALL_VALUES, client);
        fintEvents.sendDownstream(orgId, event);

        RBlockingQueue<Event<Value>> tempQueue = fintEvents.getTempQueue("test-consumer-" + event.getCorrId());
        Event<Value> receivedEvent = tempQueue.poll(30, TimeUnit.SECONDS);

        return receivedEvent.getData();
    }

    @FintEventListener
    public void receive(Event<Value> event) {
        log.info("Upstream event: {}", event);
        fintEvents.getTempQueue("test-consumer-" + event.getCorrId()).offer(event);
    }
}
