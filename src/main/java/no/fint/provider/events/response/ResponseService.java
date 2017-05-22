package no.fint.provider.events.response;

import lombok.extern.slf4j.Slf4j;
import no.fint.audit.FintAuditService;
import no.fint.event.model.Event;
import no.fint.event.model.Status;
import no.fint.events.FintEvents;
import no.fint.provider.eventstate.EventState;
import no.fint.provider.eventstate.EventStateService;
import org.redisson.api.RBlockingQueue;
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
        if (event.getAction().equals("HEALTH")) {
            RBlockingQueue<Event> healthCheckQueue = fintEvents.getTempQueue(event.getCorrId());
            event.setStatus(Status.TEMP_UPSTREAM_QUEUE);
            fintAuditService.audit(event);
            healthCheckQueue.offer(event);
            return true;
        } else {
            Optional<EventState> state = eventStateService.get(event);
            if (state.isPresent()) {
                fintAuditService.audit(event);
                event.setStatus(Status.UPSTREAM_QUEUE);
                fintEvents.sendUpstream(event.getOrgId(), event);
                fintAuditService.audit(event);
                eventStateService.remove(event);
                return true;
            } else {
                log.error("EventState with corrId {} was not found. Either the Event has expired or the provider does not recognize the corrId. action:{} status:{}", event.getCorrId(), event.getAction(), event.getStatus());
            }
        }

        return false;
    }
}
