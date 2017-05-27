package no.fint.provider.events.poll;

import lombok.extern.slf4j.Slf4j;
import no.fint.audit.FintAuditService;
import no.fint.event.model.Event;
import no.fint.event.model.Status;
import no.fint.events.FintEvents;
import no.fint.provider.events.eventstate.EventStateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.BlockingQueue;

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
        BlockingQueue<Event> queue = events.getDownstream(orgId);
        Event event = queue.poll();
        if (event != null) {
            event.setStatus(Status.DELIVERED_TO_PROVIDER);
            eventStateService.add(event, 2);
            fintAuditService.audit(event);
            return Optional.of(event);
        }

        return Optional.empty();
    }
}
