package no.fint.provider.events.sse;

import lombok.extern.slf4j.Slf4j;
import no.fint.event.model.Event;
import no.fint.events.FintEvents;
import no.fint.provider.events.subscriber.DownstreamSubscriber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Optional;

@Slf4j
@RequestMapping(value = "/provider/sse")
@RestController
public class SseController {

    @Autowired
    private SseService sseService;

    @Autowired
    private FintEvents fintEvents;

    @RequestMapping(method = RequestMethod.GET)
    public SseEmitter subscribe(@RequestHeader("x-org-id") String orgId) {
        log.info("{} connected", orgId);
        if (sseService.newListener(orgId)) {
            SseEmitter emitter = sseService.subscribe(orgId);
            fintEvents.registerDownstreamListener(orgId, DownstreamSubscriber.class);
            return emitter;
        } else {
            Optional<SseEmitter> sseEmitter = sseService.getSseEmitter(orgId);
            return (sseEmitter.isPresent()) ? sseEmitter.get() : null;
        }
    }

    @Profile("test")
    @RequestMapping(method = RequestMethod.POST)
    public void sendMessage(@RequestParam String orgId, @RequestParam String source, @RequestParam String action, @RequestParam String client) {
        sseService.send(new Event(orgId, source, action, client));
    }

}
