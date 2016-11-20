package no.fint.provider.eventstate;

import no.fint.event.model.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Profile("mock")
@Component
public class MockData {

    @Autowired
    EventStateService eventStateService;

    @PostConstruct
    public void init() {
        Event event;

        for (int i = 0; i < 10; i++) {
            event = new Event("org" + i, "fk", "GET", "client" + 1);
            eventStateService.addEventState(event);
        }


    }
}
