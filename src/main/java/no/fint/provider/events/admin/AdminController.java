package no.fint.provider.events.admin;

import com.google.common.collect.ImmutableMap;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import lombok.AccessLevel;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import no.fint.audit.plugin.mongo.MongoAuditEvent;
import no.fint.events.FintEvents;
import no.fint.provider.events.Constants;
import no.fint.provider.events.subscriber.DownstreamSubscriber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@RestController
@Api(tags = {"Admin"}, description = Constants.SWAGGER_DESC_INTERNAL_API)
@RequestMapping(value = "/admin", produces = MediaType.APPLICATION_JSON_VALUE)
public class AdminController {

    @Autowired
    private FintEvents fintEvents;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Setter(AccessLevel.PACKAGE)
    private Map<String, Long> orgIds = new ConcurrentHashMap<>();

    @GetMapping("/audit/events")
    public List<MongoAuditEvent> getAllEvents() {
        return mongoTemplate.findAll(MongoAuditEvent.class);
    }

    @DeleteMapping("/tempQueues")
    public boolean deleteTempQueues() {
        return fintEvents.deleteTempQueues();
    }

    @DeleteMapping("/clear/all")
    public void clearAll() {
        Set<String> queues = fintEvents.getQueues();
        for (String queue : queues) {
            fintEvents.getQueue(queue).clear();
        }
    }

    @DeleteMapping("/clear/downstream")
    public void clearDownstream(@ApiParam(Constants.SWAGGER_X_ORG_ID) @RequestHeader(Constants.HEADER_ORGID) String orgId) {
        fintEvents.getDownstream(orgId).clear();
    }

    @DeleteMapping("/clear/upstream")
    public void clearUpstream(@ApiParam(Constants.SWAGGER_X_ORG_ID) @RequestHeader(Constants.HEADER_ORGID) String orgId) {
        fintEvents.getUpstream(orgId).clear();
    }

    @GetMapping("/orgIds/{orgId}")
    public ResponseEntity getOrganization(@ApiParam(Constants.SWAGGER_X_ORG_ID) @PathVariable String orgId) {
        Long registered = orgIds.get(orgId);
        if (StringUtils.isEmpty(registered)) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(ImmutableMap.of(
                    "orgId", orgId,
                    "registered", registered
            ));
        }
    }

    @PostMapping("/orgIds/{orgId}")
    public ResponseEntity registerOrgId(@ApiParam(Constants.SWAGGER_X_ORG_ID) @PathVariable String orgId) {
        if (orgIds.containsKey(orgId)) {
            return ResponseEntity.badRequest().body(String.format("OrgId %s is already registered", orgId));
        } else {
            fintEvents.registerDownstreamListener(DownstreamSubscriber.class, orgId);
            orgIds.put(orgId, System.currentTimeMillis());

            URI location = ServletUriComponentsBuilder.fromCurrentRequest().buildAndExpand().toUri();
            return ResponseEntity.created(location).build();
        }
    }
}
