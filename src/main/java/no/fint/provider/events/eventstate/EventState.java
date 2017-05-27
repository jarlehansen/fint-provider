package no.fint.provider.events.eventstate;

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

    public EventState(Event event, int timeToLiveMintues) {
        updateTtl(timeToLiveMintues);
        if (event != null) {
            this.corrId = event.getCorrId();
            this.event = event;
        }
    }

    public boolean expired() {
        return (System.currentTimeMillis() > expires);
    }

    public void updateTtl(int timeToLiveMinutes) {
        expires = System.currentTimeMillis() + (timeToLiveMinutes * 60 * 1000);
    }
}
