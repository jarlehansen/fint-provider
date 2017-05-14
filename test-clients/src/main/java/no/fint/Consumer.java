package no.fint;

import no.fint.event.model.Event;
import no.fint.events.FintEvents;
import no.fint.events.FintEventsHealth;
import no.fint.events.HealthCheck;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;

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
        healthCheck = fintEventsHealth.registerClient();
    }

    @GetMapping("/healthCheck")
    public Event healthCheck(@RequestHeader("x-org-id") String orgId, @RequestHeader("x-client") String client) {
        Event<String> health = new Event<>(orgId, "test-consumer", "HEALTH", client);
        return healthCheck.check(health);
    }
}
