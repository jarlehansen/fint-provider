package no.fint.provider.eventstate;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import no.fint.event.model.Event;

import java.io.Serializable;

@ToString
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
        this.timestamp = System.currentTimeMillis();
    }

    public EventState(Event event) {
        this.timestamp = System.currentTimeMillis();
        this.event = event;
    }

    public EventState(String replyTo, Event event) {
        this.timestamp = System.currentTimeMillis();
        this.replyTo = replyTo;
        this.event = event;
    }
}
