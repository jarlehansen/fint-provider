package no.fint.provider.events.response;

import lombok.extern.slf4j.Slf4j;
import no.fint.event.model.Event;
import no.fint.provider.eventstate.EventStateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequestMapping(value = "/provider/response", consumes = MediaType.APPLICATION_JSON_VALUE)
@RestController
public class ResponseController {

    @Autowired
    private EventStateService eventStateService;

    @RequestMapping(method = RequestMethod.POST)
    public void response(Event event) {

    }
}
