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
        log.info("Event received: {}", event.getCorrId());
        if (eventStateService.expired(event)) {
            event.setStatus(Status.PROVIDER_RESPONSE_ORPHANT);
            fintAuditService.audit(event, true);
            return false;
        } else {
            fintAuditService.audit(event, true);

            log.info("EventState: {}", event.getCorrId());
            event.setStatus(Status.UPSTREAM_QUEUE);
            fintEvents.sendUpstream(event.getOrgId(), event);
            fintAuditService.audit(event, true);
            eventStateService.remove(event);
            fintEvents.getClient().getLock(event.getCorrId()).unlock();

            return true;
        }
    }
}
