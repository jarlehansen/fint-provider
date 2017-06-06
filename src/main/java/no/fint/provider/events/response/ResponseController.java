package no.fint.provider.events.response;

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
import org.springframework.web.bind.annotation.*;

@Slf4j
@Api(tags = {"response"}, description = "This endpoint is used by the adapter to post back the response.")
@RequestMapping(value = "/response", consumes = MediaType.APPLICATION_JSON_VALUE)
@RestController
public class ResponseController {

    @Autowired
    private ResponseService responseService;

    @ApiOperation(value = "", notes = "Receives event object with response data.")
    @PostMapping
    public void response(@ApiParam(Constants.SWAGGER_X_ORG_ID) @RequestHeader(Constants.HEADER_ORGID) String orgId,
                         @ApiParam(Constants.SWAGGER_EVENT) @RequestBody Event event) {
        event.setOrgId(orgId);
        responseService.handleAdapterResponse(event);
    }

    @ExceptionHandler(UnknownEventException.class)
    public ResponseEntity handleUnknownEventException() {
        return ResponseEntity.status(HttpStatus.GONE).body("Unknown Event object from adapter");
    }
}
