package no.fint.provider.events.testmode.consumer;


import com.google.common.collect.ImmutableMap;
import lombok.extern.slf4j.Slf4j;
import no.fint.event.model.Event;
import no.fint.events.FintEventListener;
import no.fint.events.FintEvents;
import no.fint.provider.events.testmode.EnabledIfTestMode;
import no.fint.provider.events.testmode.TestModeConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@EnabledIfTestMode
@Slf4j
@RestController
@RequestMapping("/test-mode-consumer")
public class TestModeController implements FintEventListener {

    @Autowired
    private FintEvents fintEvents;

    private final List<ReceivedEvent> receivedEvents = Collections.synchronizedList(new ArrayList<>());

    @PostConstruct
    public void init() {
        log.info("Test mode enabled, starting consumer");
        fintEvents.registerUpstreamListener(TestModeConstants.ORGID, this);
        fintEvents.registerUpstreamSystemListener((event -> {
            log.info("Received system event: {}", event);
            if (event.isRegisterOrgId()) {
                log.info("Registering listener for {}", event.getOrgId());
                fintEvents.registerUpstreamListener(event.getOrgId(), this);
            }
        }));
    }

    @PostMapping
    public void sendEvents(@RequestParam(defaultValue = "1", required = false) Integer numberOfEvents) {
        for (int i = 0; i < numberOfEvents; i++) {
            Event<String> event = new Event<>(TestModeConstants.ORGID, TestModeConstants.SOURCE, TestModeConstants.ACTION, TestModeConstants.CLIENT);
            fintEvents.sendDownstream(event);
        }
    }

    @PutMapping(value = "/{orgId}/{action}", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public void sendEvent(@PathVariable String orgId, @PathVariable String action, @RequestBody(required = false) String body) {
        Event event = new Event(orgId, TestModeConstants.SOURCE, action, TestModeConstants.CLIENT);
        if (body != null) {
            event.addData(body);
        }
        log.info("Sending event: {}", event);
        fintEvents.sendDownstream(event);
    }

    @GetMapping
    public Map<String, Object> getReceivedEvents() {
        return ImmutableMap.of(
                "numberOfEvents", receivedEvents.size(),
                "events", receivedEvents
        );
    }

    @Override
    public void accept(Event event) {
        log.info("Received event: {}", event.getData());
        receivedEvents.add(new ReceivedEvent(event));
    }
}
