package no.fint.consumer.person;

import com.fasterxml.jackson.core.type.TypeReference;
import no.fint.Actions;
import no.fint.Constants;
import no.fint.dto.Person;
import no.fint.event.model.Event;
import no.fint.event.model.EventUtil;
import no.fint.event.model.HeaderConstants;
import no.fint.events.FintEvents;
import no.fint.model.relation.FintResource;
import no.fint.relations.FintRelationsMediaType;
import org.redisson.api.RBlockingQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping(value = "/person", produces = FintRelationsMediaType.APPLICATION_HAL_JSON_VALUE)
public class PersonController {

    private final TypeReference<List<FintResource<Person>>> personTypeReference = new TypeReference<List<FintResource<Person>>>() {
    };

    @Autowired
    private FintEvents fintEvents;

    @Autowired
    private PersonAssembler assembler;

    @GetMapping
    public ResponseEntity getAllPersons(@RequestHeader(value = HeaderConstants.ORG_ID, defaultValue = Constants.ORGID) String orgId,
                                        @RequestHeader(value = HeaderConstants.CLIENT, defaultValue = Constants.CLIENT) String client) throws InterruptedException {
        Event<FintResource> event = new Event<>(orgId, Constants.SOURCE, Actions.GET_ALL_PERSONS, client);
        fintEvents.sendDownstream(orgId, event);

        RBlockingQueue<Event<FintResource>> tempQueue = fintEvents.getTempQueue("test-consumer-" + event.getCorrId());
        Event<FintResource> receivedEvent = tempQueue.poll(30, TimeUnit.SECONDS);

        List<FintResource<Person>> fintResources = EventUtil.convertEventData(receivedEvent, personTypeReference);

        return assembler.resources(fintResources);
    }

    @GetMapping("/{id}")
    public ResponseEntity getPerson(@PathVariable String id,
                                    @RequestHeader(value = HeaderConstants.ORG_ID, defaultValue = Constants.ORGID) String orgId,
                                    @RequestHeader(value = HeaderConstants.CLIENT, defaultValue = Constants.CLIENT) String client) throws InterruptedException {
        Event<String> event = new Event<>(orgId, Constants.SOURCE, Actions.GET_PERSON, client);
        event.setQuery(id);
        fintEvents.sendDownstream(orgId, event);

        RBlockingQueue<Event<FintResource>> tempQueue = fintEvents.getTempQueue("test-consumer-" + event.getCorrId());
        Event<FintResource> receivedEvent = tempQueue.poll(30, TimeUnit.SECONDS);
        List<FintResource<Person>> fintResources = EventUtil.convertEventData(receivedEvent, personTypeReference);

        return assembler.resource(fintResources.get(0));
    }

}
