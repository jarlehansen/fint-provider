package no.fint.provider.events.poll;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import no.fint.event.model.Event;
import no.fint.event.model.HeaderConstants;
import no.fint.provider.events.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@Slf4j
@Api(tags = {"poll"}, description = "This endpoint is for handling polling clients.")
@RestController
@RequestMapping(value = "/poll", produces = MediaType.APPLICATION_JSON_VALUE)
public class PollController {

    @Autowired
    private PollService pollService;

    @GetMapping
    public ResponseEntity poll(@ApiParam(Constants.SWAGGER_X_ORG_ID) @RequestHeader(HeaderConstants.ORG_ID) String orgId) {
        Optional<Event> event = pollService.readEvent(orgId);
        return event.<ResponseEntity>map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.noContent().build());
    }
}
