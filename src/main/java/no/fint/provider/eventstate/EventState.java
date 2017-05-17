package no.fint.provider.eventstate;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import no.fint.event.model.Event;

@Data
@NoArgsConstructor
@EqualsAndHashCode(of = "corrId")
public class EventState {
    private String corrId;
    private long expires;

    private Event event;

    public EventState(Event event) {
        this.corrId = event.getCorrId();
        this.event = event;
    }

    public EventState(Event event, int ttl) {
        updateTtl(ttl);
        if (event != null) {
            this.corrId = event.getCorrId();
            this.event = event;
        }
    }

    public boolean expired() {
        return (System.currentTimeMillis() > expires);
    }

    public void updateTtl(int ttl) {
        expires = System.currentTimeMillis() + (ttl * 60 * 1000);
    }
}
