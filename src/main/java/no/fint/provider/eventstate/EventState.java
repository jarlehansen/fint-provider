package no.fint.provider.eventstate;

import lombok.Data;
import lombok.EqualsAndHashCode;
import no.fint.event.model.Event;

@Data
@EqualsAndHashCode(of = "corrId")
public class EventState {
    private String corrId;
    private long timestamp;

    private String orgId;
    private String source;
    private String action;
    private String client;

    public EventState(Event event) {
        this.timestamp = System.currentTimeMillis();
        if (event != null) {
            this.corrId = event.getCorrId();
            this.orgId = event.getOrgId();
            this.source = event.getSource();
            this.action = event.getAction();
            this.client = event.getClient();
        }
    }
}
