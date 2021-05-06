package no.fint.provider.eventstate;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import no.fint.provider.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@Slf4j
@RestController
@Api(tags = {"eventstates"}, description = Constants.SWAGGER_DESC_INTERNAL_API)

@RequestMapping(value = "/eventStates", produces = MediaType.APPLICATION_JSON_VALUE)
public class EventStateController {

    @Autowired
    private EventStateService eventStateService;

    @GetMapping
    public Collection<EventState> getEventState() {
        return eventStateService.getEventStates();
    }

}
