package no.fint.provider.events.status;

import lombok.extern.slf4j.Slf4j;
import no.fint.event.model.Event;
import no.fint.provider.exceptions.UnknownEventException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping(value = "/provider/status", consumes = MediaType.APPLICATION_JSON_VALUE)
public class StatusController {

    @Autowired
    private StatusService statusService;

    @RequestMapping(method = RequestMethod.POST)
    public void status(@RequestHeader("x-org-id") String orgId, @RequestBody Event event) {
        event.setOrgId(orgId);
        statusService.updateEventState(event);
    }

    @ExceptionHandler(HttpMessageConversionException.class)
    public ResponseEntity handleHttpMessageConversionException() {
        return ResponseEntity.badRequest().body("Invalid Event object from adapter");
    }

    @ExceptionHandler(UnknownEventException.class)
    public ResponseEntity handleUnknownEventException() {
        return ResponseEntity.badRequest().body("Unknown Event object from adapter");
    }
}
