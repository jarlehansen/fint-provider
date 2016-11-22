package no.fint.provider.status;

import lombok.extern.slf4j.Slf4j;
import no.fint.event.model.Event;
import no.fint.provider.eventstate.EventStateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(value = "/provider/status", consumes = MediaType.APPLICATION_JSON_VALUE)
public class StatusController {

    @Autowired
    private EventStateService eventStateService;

    @RequestMapping(method = RequestMethod.POST)
    public void status(Event event) {
        eventStateService.addEventState(event);
    }

}
