package no.fint.provider.events.response;

import lombok.extern.slf4j.Slf4j;
import no.fint.event.model.Event;
import no.fint.provider.events.exceptions.UnknownEventException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequestMapping(value = "/response", consumes = MediaType.APPLICATION_JSON_VALUE)
@RestController
public class ResponseController {

    @Autowired
    private ResponseService responseService;

    @PostMapping
    public void response(@RequestHeader("x-org-id") String orgId, @RequestBody Event event) {
        event.setOrgId(orgId);
        responseService.handleAdapterResponse(event);
    }

    @ExceptionHandler(UnknownEventException.class)
    public ResponseEntity handleUnknownEventException() {
        return ResponseEntity.status(HttpStatus.GONE).body("Unknown Event object from adapter");
    }
}
