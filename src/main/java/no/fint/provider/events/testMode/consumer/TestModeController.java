package no.fint.provider.events.testMode.consumer;


import com.google.common.collect.ImmutableMap;
import lombok.extern.slf4j.Slf4j;
import no.fint.event.model.Event;
import no.fint.events.FintEventListener;
import no.fint.events.FintEvents;
import no.fint.provider.events.testMode.TestModeConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@ConditionalOnProperty(name = "fint.provider.test-mode", havingValue = "true")
@RestController
@RequestMapping("/test-mode-consumer")
public class TestModeController implements FintEventListener {

    @Autowired
    private FintEvents fintEvents;

    private List<Event> receivedEvents = Collections.synchronizedList(new ArrayList<>());

    @PostConstruct
    public void init() {
        fintEvents.registerUpstreamListener(TestModeConstants.ORGID, this);
    }

    @PostMapping
    public void sendEvent(@RequestParam(defaultValue = "1", required = false) Integer numberOfEvents) {
        for(int i = 0; i < numberOfEvents; i++) {
            Event<String> event = new Event<>(TestModeConstants.ORGID, TestModeConstants.SOURCE, TestModeConstants.ACTION, TestModeConstants.CLIENT);
            fintEvents.sendDownstream(event);
        }
    }

    @GetMapping
    public List<Map<String, String>> getReceivedEvents() {
        return receivedEvents.stream().map(event ->
                ImmutableMap.of(
                        "corrId", event.getCorrId(),
                        "status", event.getStatus().name(),
                        "data", event.getData().get(0).toString()
                )
        ).collect(Collectors.toList());
    }

    @Override
    public void accept(Event event) {
        log.info("Received event: {}", event.getData());
        receivedEvents.add(event);
    }
}
