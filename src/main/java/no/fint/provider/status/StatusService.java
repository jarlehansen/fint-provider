package no.fint.provider.status;

import lombok.extern.slf4j.Slf4j;
import no.fint.audit.FintAuditService;
import no.fint.event.model.Event;
import no.fint.event.model.ResponseStatus;
import no.fint.event.model.Status;
import no.fint.events.FintEvents;
import no.fint.provider.ProviderProps;
import no.fint.provider.eventstate.EventState;
import no.fint.provider.eventstate.EventStateService;
import no.fint.provider.exceptions.UnknownEventException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class StatusService {

    @Autowired
    private EventStateService eventStateService;

    @Autowired
    private FintAuditService fintAuditService;

    @Autowired
    private FintEvents fintEvents;

    @Autowired
    private ProviderProps providerProps;

    public void updateEventState(Event event) {
        log.trace("Event received: {}", event);
        Optional<EventState> state = eventStateService.remove(event);
        if (state.isPresent()) {
            fintAuditService.audit(event);

            if (event.getStatus() == Status.ADAPTER_ACCEPTED) {
                eventStateService.add(event, providerProps.getResponseTtl());
            } else {
                sendResponse(event);
            }
        } else {
            throw new UnknownEventException(event.getCorrId());
        }
    }

    private void sendResponse(Event event) {
        if (event.getResponseStatus() == null) {
            event.setResponseStatus(ResponseStatus.REJECTED);
        }
        log.debug("{} adapter did not acknowledge the event (status: {}), sending event upstream.", event.getOrgId(), event.getStatus());
        fintEvents.sendUpstream(event);
    }

}
