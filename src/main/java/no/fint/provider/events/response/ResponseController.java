package no.fint.provider.events.response;

import lombok.extern.slf4j.Slf4j;
import no.fint.event.model.Event;
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
    public ResponseEntity response(@RequestHeader("x-org-id") String orgId, @RequestBody Event event) {
        event.setOrgId(orgId);
        boolean responseHandled = responseService.handleAdapterResponse(event);
        if (responseHandled) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.GONE).build();
        }
    }
}
