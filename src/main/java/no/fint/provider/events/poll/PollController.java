package no.fint.provider.events.poll;


import lombok.extern.slf4j.Slf4j;
import no.fint.event.model.Event;
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
@RestController
@RequestMapping(value = "/poll", produces = MediaType.APPLICATION_JSON_VALUE)
public class PollController {

    @Autowired
    private PollService pollService;

    @GetMapping
    public ResponseEntity poll(@RequestHeader(Constants.HEADER_ORGID) String orgId) {
        Optional<Event> event = pollService.readEvent(orgId);
        return event.<ResponseEntity>map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.noContent().build());
    }
}
