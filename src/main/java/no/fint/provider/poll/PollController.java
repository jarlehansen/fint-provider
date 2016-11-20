package no.fint.provider.poll;


import lombok.extern.slf4j.Slf4j;
import no.fint.event.model.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@Slf4j
@RestController
@RequestMapping(value = "/provider/poll", produces = MediaType.APPLICATION_JSON_VALUE)
public class PollController {

    @Autowired
    private PollService pollService;
    
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity poll(@RequestHeader("x-org-id") String orgId) {
        Optional<Event> event = pollService.readEvent(orgId);
        if (event.isPresent()) {
            return ResponseEntity.ok(event.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
