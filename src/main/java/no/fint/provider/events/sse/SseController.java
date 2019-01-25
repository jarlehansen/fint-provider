package no.fint.provider.events.sse;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import no.fint.event.model.HeaderConstants;
import no.fint.events.FintEvents;
import no.fint.provider.events.Constants;
import no.fint.provider.events.admin.AdminService;
import no.fint.provider.events.subscriber.DownstreamSubscriber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
    private AdminService adminService;

    @Autowired
    private SseService sseService;

    @Autowired
    private FintEvents fintEvents;

    @Autowired
    private DownstreamSubscriber downstreamSubscriber;

    @ApiOperation(value = "Connect SSE client", notes = "Endpoint to register SSE client.")
    @GetMapping("/{id}")
    public ResponseEntity<SseEmitter> subscribe(
            @ApiParam(Constants.SWAGGER_X_ORG_ID) @RequestHeader(HeaderConstants.ORG_ID) String orgId,
            @ApiParam("ID of client.") @RequestHeader(HeaderConstants.CLIENT) String client,
            @ApiParam("Global unique id for the client. Typically a UUID.") @PathVariable String id) {
        log.info("{}: Client {}, ID {}", orgId, client, id);
        if (adminService.register(orgId, client)) {
            SseEmitter emitter = sseService.subscribe(id, orgId, client);
            fintEvents.registerDownstreamListener(orgId, downstreamSubscriber);
            return ResponseEntity.ok(emitter);
        } else {
            return ResponseEntity.badRequest().header("x-Error", "Invalid orgID " + orgId).build();
        }
    }

    @ApiOperation(value = "", notes = "Returns all registered SSE clients.")
    @GetMapping("/clients")
    public List<SseOrg> getClients() {
        Map<String, FintSseEmitters> clients = sseService.getSseClients();
        List<SseOrg> orgs = new ArrayList<>();
        clients.forEach((key, value) -> {
            List<SseClient> sseClients = new ArrayList<>();
            value.forEach(emitter -> sseClients.add(new SseClient(emitter.getRegistered(), emitter.getId(), emitter.getClient(), emitter.getEventCounter().get())));

            orgs.add(new SseOrg(key, sseClients));
        });
        return orgs;
    }

    @ApiOperation(value = "", notes = "Remove all registered SSE clients.")
    @DeleteMapping("/clients")
    public void removeSseClients() {
        sseService.removeAll();
    }

    @ApiOperation(value = "", notes = "Adapter starts the oauth process")
    @GetMapping("/auth-init")
    public void authorize() {
    }

}
