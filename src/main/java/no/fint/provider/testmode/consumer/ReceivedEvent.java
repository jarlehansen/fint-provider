package no.fint.provider.testmode.consumer;

import lombok.Getter;
import no.fint.event.model.Event;
import no.fint.event.model.Status;

@Getter
class ReceivedEvent {
    private String corrId;
    private String action;
    private Status status;
    private String data;

    ReceivedEvent(Event event) {
        this.corrId = event.getCorrId();
        this.action = event.getAction();
        this.status = event.getStatus();
        if (event.getData().size() > 0) {
            this.data = event.getData().get(0).toString();
        }
    }
}
