package no.fint.provider.events.poll;

import lombok.extern.slf4j.Slf4j;
import no.fint.audit.FintAuditService;
import no.fint.event.model.Event;
import no.fint.event.model.Status;
import no.fint.events.FintEvents;
import no.fint.provider.eventstate.EventStateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class PollService {

    @Autowired
    private FintEvents events;

    @Autowired
    private EventStateService eventStateService;

    @Autowired
    private FintAuditService fintAuditService;

    public Optional<Event> readEvent(String orgId) {
        Optional<Event> optionalEvent = events.readDownstream(orgId, Event.class);
        if (optionalEvent.isPresent()) {
            Event event = optionalEvent.get();
            event.setStatus(Status.DELIVERED_TO_PROVIDER);
            eventStateService.add(event);
            fintAuditService.audit(event, true);
            return Optional.of(event);
        }

        return Optional.empty();
    }
}
