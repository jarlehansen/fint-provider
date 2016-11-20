package no.fint.provider.admin;

import no.fint.provider.eventstate.EventState;
import no.fint.provider.eventstate.EventStateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping(value = "/admin", produces = MediaType.APPLICATION_JSON_VALUE)
public class AdminController {

    @Autowired
    private EventStateService eventStateService;

    @RequestMapping(value = "/eventStates", method = RequestMethod.GET)
    public Map<String, EventState> getEventState() {
        return eventStateService.getEventStateMap();
    }
}
