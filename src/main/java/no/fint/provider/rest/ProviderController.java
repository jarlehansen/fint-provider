package no.fint.provider.rest;

import com.google.common.collect.ImmutableMap;
import no.fint.events.FintEvents;
import org.springframework.amqp.core.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping(value = "/provider", produces = MediaType.APPLICATION_JSON_VALUE)
public class ProviderController {

    @Autowired
    private FintEvents events;

    @RequestMapping(method = RequestMethod.GET)
    public ImmutableMap<String, String> receive(@RequestHeader("x-org-id") String orgId) {
        Optional<Message> message = events.readDownstreamMessage(orgId);
        if (message.isPresent()) {
            return ImmutableMap.of("value", new String(message.get().getBody()));
        } else {
            return ImmutableMap.of();
        }
    }

    @RequestMapping(value = "/organizations", method = RequestMethod.POST)
    public void createOrganization(@RequestBody String orgId) {
        events.addOrganization(orgId);
    }

    @RequestMapping(value = "/organizations", method = RequestMethod.DELETE)
    public void removeOrganization(@RequestBody String orgId) {
        events.removeOrganization(orgId);
    }
}
