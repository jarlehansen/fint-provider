package no.fint.rest;

import no.fint.events.FintEvents;
import org.springframework.amqp.core.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping(value = "/provider")
public class ProviderController {

    @Autowired
    private FintEvents events;

    @RequestMapping(method = RequestMethod.GET)
    public String receive(@RequestHeader("x-org-id") String orgId) {
        Optional<Message> message = events.readInputMessage(orgId);
        if (message.isPresent()) {
            return new String(message.get().getBody());
        } else {
            return "";
        }
    }
}
