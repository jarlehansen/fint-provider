package no.fint.provider.events.sse;

import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;
import no.fint.events.FintEvents;
import no.fint.provider.events.Constants;
import no.fint.provider.events.subscriber.DownstreamSubscriber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@RequestMapping(value = "/sse")
@RestController
public class SseController {

    @Autowired
    private SseService sseService;

    @Autowired
    private FintEvents fintEvents;

    @Synchronized
    @GetMapping("/{id}")
    public SseEmitter subscribe(@RequestHeader(Constants.HEADER_ORGID) String orgId, @PathVariable String id) {
        SseEmitter emitter = sseService.subscribe(id, orgId);
        fintEvents.registerDownstreamListener(DownstreamSubscriber.class, orgId);
        return emitter;
    }

    @GetMapping("/clients")
    public List<SseOrg> getClients() {
        Map<String, FintSseEmitters> clients = sseService.getSseClients();
        log.info("Connected SSE clients: {}", clients);

        List<SseOrg> orgs = new ArrayList<>();
        clients.forEach((key, value) -> {
            List<SseClient> sseClients = new ArrayList<>();
            value.forEach(emitter -> sseClients.add(new SseClient(emitter.getRegistered(), emitter.getId())));

            orgs.add(new SseOrg(key, sseClients));
        });
        return orgs;
    }

    @DeleteMapping("/clients")
    public void removeSseClients() {
        sseService.removeAll();
    }


}
