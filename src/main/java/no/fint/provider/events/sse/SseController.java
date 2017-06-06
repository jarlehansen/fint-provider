package no.fint.provider.events.sse;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
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
@Api(tags = {"sse"}, description = "These endpoint is for handling SSE clients.")
@RequestMapping(value = "/sse")
@RestController
public class SseController {

    @Autowired
    private SseService sseService;

    @Autowired
    private FintEvents fintEvents;

    @ApiOperation(value = "Connect SSE client", notes = "Endpoint to register SSE client.")
    @Synchronized
    @GetMapping("/{id}")
    public SseEmitter subscribe(@ApiParam(Constants.SWAGGER_X_ORG_ID) @RequestHeader(Constants.HEADER_ORGID) String orgId,
                                @ApiParam("Global unique id for the client. Typically a UUID.") @PathVariable String id) {
        SseEmitter emitter = sseService.subscribe(id, orgId);
        fintEvents.registerDownstreamListener(DownstreamSubscriber.class, orgId);
        return emitter;
    }

    @ApiOperation(value = "", notes = "Returns all registered SSE clients.")
    @GetMapping("/clients")
    public List<SseOrg> getClients() {
        Map<String, FintSseEmitters> clients = sseService.getSseClients();
        List<SseOrg> orgs = new ArrayList<>();
        clients.forEach((key, value) -> {
            List<SseClient> sseClients = new ArrayList<>();
            value.forEach(emitter -> sseClients.add(new SseClient(emitter.getRegistered(), emitter.getId())));

            orgs.add(new SseOrg(key, sseClients));
        });
        return orgs;
    }

    @ApiOperation(value = "", notes = "Remove all registered SSE clients.")
    @DeleteMapping("/clients")
    public void removeSseClients() {
        sseService.removeAll();
    }


}
