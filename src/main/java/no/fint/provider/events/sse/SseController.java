package no.fint.provider.events.sse;

import lombok.extern.slf4j.Slf4j;
import no.fint.event.model.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Slf4j
@RequestMapping(value = "/provider/sse")
@RestController
public class SseController {

    @Autowired
    private SseService sseService;

    @RequestMapping(method = RequestMethod.GET)
    public SseEmitter subscribe(@RequestHeader("x-org-id") String orgId) {
        SseEmitter emitter = sseService.subscribe(orgId);
        log.info("{} connected", orgId);
        return emitter;
    }

    @Profile("test")
    @RequestMapping(method = RequestMethod.POST)
    public void sendMessage(@RequestParam String orgId, @RequestParam String source, @RequestParam String verb, @RequestParam String client) {
        sseService.send(new Event(orgId, source, verb, client));
    }

}
