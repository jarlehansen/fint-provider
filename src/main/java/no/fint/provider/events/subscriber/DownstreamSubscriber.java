package no.fint.provider.events.subscriber;

import lombok.extern.slf4j.Slf4j;
import no.fint.audit.FintAuditService;
import no.fint.event.model.Event;
import no.fint.event.model.Status;
import no.fint.provider.events.sse.SseService;
import no.fint.provider.eventstate.EventState;
import no.fint.provider.eventstate.EventStateService;
import no.fint.provider.exceptions.EventNotApprovedByProviderException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component
public class DownstreamSubscriber {

    @Autowired
    private SseService sseService;

    @Autowired
    private EventStateService eventStateService;

    @Autowired
    private FintAuditService fintAuditService;

    public void receive(String replyTo, Event event) {
        log.info("Event received: {} - reply to: {}", event, replyTo);
        if (eventStateService.exists(event)) {
            Optional<EventState> eventState = eventStateService.getEventState(event);
            eventState.ifPresent(es -> handleEvent(es.getEvent()));
        } else {
            sendInitialEvent(replyTo, event);
        }
    }

    private void handleEvent(Event event) {
        if (eventStateService.exists(event, Status.DELIVERED_TO_PROVIDER)) {
            sseService.send(event);
            throw new EventNotApprovedByProviderException();
        } else {
            log.info("Event with corrId:{} approved by adapter. Consuming message from queue", event.getCorrId());
        }
    }

    private void sendInitialEvent(String replyTo, Event event) {
        event.setStatus(Status.DELIVERED_TO_PROVIDER);
        sseService.send(event);
        eventStateService.add(replyTo, event);
        fintAuditService.audit(event, true);

        try {
            Thread.sleep(3000L);
        } catch (InterruptedException ignored) {
        }

        if (eventStateService.exists(event, Status.DELIVERED_TO_PROVIDER)) {
            throw new EventNotApprovedByProviderException();
        } else {
            log.info("Event with corrId:{} approved by adapter. Consuming message from queue", event.getCorrId());
        }
    }

}
