package no.fint.provider.events.testMode.consumer;

import lombok.Getter;
import no.fint.event.model.Event;
import no.fint.event.model.Status;

@Getter
class ReceivedEvent {
    private String corrId;
    private Status status;
    private String data;

    ReceivedEvent(Event event) {
        this.corrId = event.getCorrId();
        this.status = event.getStatus();
        this.data = event.getData().get(0).toString();
    }
}
