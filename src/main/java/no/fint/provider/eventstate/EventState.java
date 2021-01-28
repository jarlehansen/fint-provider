package no.fint.provider.eventstate;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import no.fint.event.model.Event;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

@Getter
@EqualsAndHashCode(of = "corrId")
public class EventState implements Serializable {
    private final String corrId;
    private final long expires;
    private final Event event;

    public EventState(Event event, int timeToLiveMinutes) {
        this.event = event;
        corrId = event.getCorrId();
        expires = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(timeToLiveMinutes);
    }

    public boolean expired() {
        return (System.currentTimeMillis() > expires);
    }

}
