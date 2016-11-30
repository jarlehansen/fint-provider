package no.fint.provider.eventstate;

import lombok.Getter;
import lombok.Setter;
import no.fint.event.model.Event;

import java.io.Serializable;

public class EventState implements Serializable {
    @Getter
    private long timestamp;
    @Getter
    @Setter
    private String replyTo;
    @Getter
    @Setter
    private Event event;

    public EventState() {
        timestamp = System.currentTimeMillis();
    }

    public EventState(Event e) {
        timestamp = System.currentTimeMillis();
        event = e;
    }
}
