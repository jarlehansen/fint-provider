package no.fint.provider.events.response;

import lombok.extern.slf4j.Slf4j;
import no.fint.audit.FintAuditService;
import no.fint.event.model.Event;
import no.fint.event.model.Status;
import no.fint.events.FintEvents;
import no.fint.provider.eventstate.EventState;
import no.fint.provider.eventstate.EventStateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

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
        Optional<EventState> eventState = eventStateService.get(event.getCorrId());
        if (eventState.isPresent()) {
            fintAuditService.audit(event, true);

            log.info("EventState: {}", eventState.get());
            String replyTo = eventState.get().getReplyTo();
            if (replyTo == null) {
                event.setStatus(Status.UPSTREAM_QUEUE);
                fintEvents.sendUpstream(event.getOrgId(), event);
            } else {
                event.setStatus(Status.TEMP_UPSTREAM_QUEUE);
                // fintEvents.reply(replyTo, event);
            }
            fintAuditService.audit(event, true);
            eventStateService.clear(event);
            return true;
        } else {
            event.setStatus(Status.PROVIDER_RESPONSE_ORPHANT);
            fintAuditService.audit(event, true);
            return false;
        }
    }
}
