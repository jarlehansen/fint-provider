package no.fint.provider.events.eventstate;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import no.fint.audit.FintAuditService;
import no.fint.event.model.Event;
import no.fint.event.model.Status;
import no.fint.events.FintEvents;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class JanitorService {

    @Autowired
    private EventStateService eventStateService;

    @Autowired
    private FintAuditService fintAuditService;

    @Autowired
    private FintEvents fintEvents;

    @Scheduled(initialDelay = 20000L, fixedDelay = 5000L)
    public void cleanUpEventStates() {
        log.debug("Running janitor service");
        List<EventState> eventStates = Lists.newArrayList(eventStateService.getEventStates());
        eventStates.forEach(eventState -> {
            if (eventState.expired()) {
                Event event = eventState.getEvent();
                log.info("EventState expired, removing from list: {}", eventState);
                eventStateService.remove(event);

                event.setStatus(Status.ADAPTER_NOT_CONFIRMED);
                event.setMessage("Event expired");
                fintAuditService.audit(event);
                fintEvents.sendUpstream(event);
            }
        });
    }
}
