package no.fint.provider.events.testMode.consumer;


import lombok.extern.slf4j.Slf4j;
import no.fint.event.model.Event;
import no.fint.events.FintEventListener;
import no.fint.events.FintEvents;
import no.fint.provider.events.testMode.TestModeConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;

@Slf4j
@ConditionalOnProperty(name = "fint.provider.test-mode", havingValue = "true")
@RestController
@RequestMapping("/test-mode-consumer")
public class TestModeController implements FintEventListener {

    @Autowired
    private FintEvents fintEvents;

    @PostConstruct
    public void init() {
        fintEvents.registerUpstreamListener(TestModeConstants.ORGID, this);
    }

    @GetMapping
    public ResponseEntity get() {
        Event<String> event = new Event<>(TestModeConstants.ORGID, TestModeConstants.SOURCE, "GET", TestModeConstants.CLIENT);
        fintEvents.sendDownstream(event);
        return ResponseEntity.ok().build();
    }

    @Override
    public void accept(Event event) {
        log.info("Received event: {}", event.getData());
    }
}
