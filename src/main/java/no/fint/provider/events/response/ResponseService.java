package no.fint.provider.events.response;

import lombok.extern.slf4j.Slf4j;
import no.fint.audit.FintAuditService;
import no.fint.event.model.Event;
import no.fint.event.model.Status;
import no.fint.events.FintEvents;
import no.fint.provider.eventstate.EventStateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ResponseService {

    @Autowired
    private EventStateService eventStateService;

    @Autowired
    private FintAuditService fintAuditService;

    @Autowired
    private FintEvents fintEvents;

    public boolean handleAdapterResponse(Event event) {
        if (eventStateService.exists(event)) {
            fintAuditService.audit(event, true);
            event.setStatus(Status.UPSTREAM_QUEUE);
            fintAuditService.audit(event, true);
            fintEvents.sendUpstreamObject(event.getOrgId(), event);
            eventStateService.clearEventState(event);
            return true;
        } else {
            event.setStatus(Status.PROVIDER_RESPONSE_ORPHANT);
            fintAuditService.audit(event, true);
            return false;
        }
    }
}
