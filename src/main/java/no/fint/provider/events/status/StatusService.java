package no.fint.provider.events.status;

import lombok.extern.slf4j.Slf4j;
import no.fint.audit.FintAuditService;
import no.fint.event.model.Event;
import no.fint.event.model.Status;
import no.fint.events.FintEvents;
import no.fint.provider.events.ProviderProps;
import no.fint.provider.events.eventstate.EventState;
import no.fint.provider.events.eventstate.EventStateService;
import no.fint.provider.events.exceptions.UnknownEventException;
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
        Optional<EventState> state = eventStateService.get(event);
        if (state.isPresent()) {
            fintAuditService.audit(event);
            EventState eventState = state.get();

            if (event.getStatus() == Status.ADAPTER_ACCEPTED) {
                eventState.updateTtl(providerProps.getResponseTtl());
            } else {
                log.info("Adapter did not acknowledge the event (status: {}), sending event upstream.", event.getStatus().name());
                event.setMessage(String.format("Adapter did not acknowledge the event (status: %s)", event.getStatus().name()));
                fintAuditService.audit(event);
                fintEvents.sendUpstream(event.getOrgId(), event);
                eventStateService.remove(event);
            }
        } else {
            log.error("EventState with corrId {} was not found. Either the Event has expired or the provider does not recognize the corrId. {}", event.getCorrId(), event);
            throw new UnknownEventException();
        }
    }

}
