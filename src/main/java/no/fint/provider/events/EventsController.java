package no.fint.provider.events;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import no.fint.event.model.Event;
import no.fint.event.model.HeaderConstants;
import no.fint.events.FintEvents;
import no.fint.provider.Constants;
import no.fint.provider.admin.AdminService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@Api(tags = {"events"}, description = Constants.SWAGGER_DESC_INTERNAL_API)
@RequestMapping(value = "/events", produces = MediaType.APPLICATION_JSON_VALUE)
public class EventsController {

    private final EventsService eventsService;
    private final AdminService adminService;
    private final FintEvents fintEvents;

    public EventsController(EventsService eventsService, AdminService adminService, FintEvents fintEvents) {
        this.eventsService = eventsService;
        this.adminService = adminService;
        this.fintEvents = fintEvents;
    }

    @PostMapping
    public ResponseEntity<Void> registerSubscriber(
            @RequestHeader(HeaderConstants.ORG_ID) String orgId,
            @RequestHeader(HeaderConstants.CLIENT) String client
    ) {
        log.info("Register {}: {}", orgId, client);
        if (adminService.register(orgId, client)) {
            eventsService.register(orgId);
            fintEvents.registerDownstreamListener(orgId, eventsService);
            return ResponseEntity.accepted().build();
        } else {
            return ResponseEntity.badRequest().header("x-Error", "Invalid orgID " + orgId).build();
        }
    }

    @DeleteMapping
    public ResponseEntity<Void> unregisterSubscriber(
            @RequestHeader(HeaderConstants.ORG_ID) String orgId,
            @RequestHeader(HeaderConstants.CLIENT) String client
    ) {
        log.info("Unregister {}: {}", orgId, client);
        eventsService.unregister(orgId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public List<Event> getPendingEvents(
            @RequestHeader(HeaderConstants.ORG_ID) String orgId,
            @RequestHeader(HeaderConstants.CLIENT) String client
    ) {
        log.info("Get events for {}: {}", orgId, client);
        return eventsService.drainEvents(orgId);
    }
}
