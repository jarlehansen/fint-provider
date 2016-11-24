package no.fint.provider.events.response;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import no.fint.audit.FintAuditService;
import no.fint.event.model.Event;
import no.fint.event.model.Status;
import no.fint.events.Events;
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
    private ObjectMapper objectMapper;

    @Autowired
    private Events events;

    public boolean handleAdapterResponse(Event event) {
        if (eventStateService.exists(event)) {
            try {
                event.setStatus(Status.UPSTREAM_QUEUE);
                fintAuditService.audit(event, true);
                String json = objectMapper.writeValueAsString(event);

                // TODO add helper method to send messages back to rabbitmq
                events.rabbitTemplate().convertAndSend(event.getOrgId(), event.getOrgId() + ".upstream", json);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }

            eventStateService.clearEventState(event);
            return true;
        } else {
            event.setStatus(Status.PROVIDER_RESPONSE_ORPHANT);
            fintAuditService.audit(event, true);
            return false;
        }
    }
}
