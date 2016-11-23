package no.fint.provider.events.subscriber;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.fint.event.model.Event;
import no.fint.provider.eventstate.EventStateService;
import no.fint.provider.events.sse.SseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class DownstreamSubscriber {

    @Autowired
    private SseService sseService;

    @Autowired
    private EventStateService eventStateService;

    @Autowired
    private ObjectMapper objectMapper;

    public void receive(byte[] body) {
        try {
            Event event = objectMapper.readValue(body, Event.class);
            sseService.send(event);
            eventStateService.addEventState(event);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
