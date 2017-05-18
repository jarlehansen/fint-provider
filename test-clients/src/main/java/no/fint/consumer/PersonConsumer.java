package no.fint.consumer;

import com.fasterxml.jackson.core.type.TypeReference;
import no.fint.Actions;
import no.fint.Constants;
import no.fint.dto.Person;
import no.fint.event.model.Event;
import no.fint.event.model.EventUtil;
import no.fint.events.FintEvents;
import no.fint.model.relation.FintResource;
import no.fint.relations.annotations.FintRelations;
import no.fint.relations.annotations.FintSelf;
import org.redisson.api.RBlockingQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.TimeUnit;

@FintSelf(type = Person.class, property = "id")
@RestController
@RequestMapping(produces = {"application/hal+json"})
public class PersonConsumer {

    @Autowired
    private FintEvents fintEvents;

    @FintRelations
    @GetMapping("/person")
    public ResponseEntity getAllPersons(@RequestHeader(value = Constants.HEADER_ORGID, defaultValue = Constants.ORGID) String orgId,
                                        @RequestHeader(value = Constants.HEADER_CLIENT, defaultValue = Constants.CLIENT) String client) throws InterruptedException {
        Event<FintResource> event = new Event<>(orgId, Constants.SOURCE, Actions.GET_ALL_PERSONS, client);
        fintEvents.sendDownstream(orgId, event);

        RBlockingQueue<Event<FintResource>> tempQueue = fintEvents.getTempQueue("test-consumer-" + event.getCorrId());
        Event<FintResource> receivedEvent = tempQueue.poll(30, TimeUnit.SECONDS);

        List<FintResource<Person>> fintResources = EventUtil.convertEventData(receivedEvent, new TypeReference<List<FintResource<Person>>>() {
        });

        return ResponseEntity.ok(fintResources);
    }

}
