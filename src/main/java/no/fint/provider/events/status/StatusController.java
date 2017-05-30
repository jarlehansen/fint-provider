package no.fint.provider.events.status;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import no.fint.event.model.Event;
import no.fint.provider.events.Constants;
import no.fint.provider.events.exceptions.UnknownEventException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Api(tags = {"status"}, description = "Endpoint where the adapter should report that the event is received and understood.")
@RestController
@RequestMapping(value = "/status", consumes = MediaType.APPLICATION_JSON_VALUE)
public class StatusController {

    @Autowired
    private StatusService statusService;

    @ApiOperation(value = "", notes = "Post back the event with the status flag telling if the response can be handled or not.")
    @PostMapping
    public void status(@ApiParam(value = Constants.SWAGGER_X_ORG_ID) @RequestHeader(Constants.HEADER_ORGID) String orgId,
                       @ApiParam(value = Constants.SWAGGER_EVENT) @RequestBody Event event) {
        event.setOrgId(orgId);
        statusService.updateEventState(event);
    }

    @ExceptionHandler(HttpMessageConversionException.class)
    public ResponseEntity handleHttpMessageConversionException() {
        return ResponseEntity.badRequest().body("Invalid Event object from adapter");
    }

    @ExceptionHandler(UnknownEventException.class)
    public ResponseEntity handleUnknownEventException() {
        return ResponseEntity.status(HttpStatus.GONE).body("Unknown Event object from adapter");
    }
}
