package no.fint.consumer;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.Lists;
import no.fint.Actions;
import no.fint.Constants;
import no.fint.dto.Address;
import no.fint.event.model.Event;
import no.fint.event.model.EventUtil;
import no.fint.events.FintEvents;
import no.fint.model.relation.FintResource;
import no.fint.relations.annotations.FintRelations;
import no.fint.relations.annotations.FintSelf;
import org.redisson.api.RBlockingQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.TimeUnit;

@FintSelf(type = Address.class, property = "id")
@RestController
@RequestMapping(value = "/address", produces = {"application/hal+json"})
public class AddressConsumer {

    @Autowired
    private FintEvents fintEvents;

    @FintRelations
    @GetMapping
    public ResponseEntity getAllAddresses(@RequestHeader(value = Constants.HEADER_ORGID, defaultValue = Constants.ORGID) String orgId,
                                          @RequestHeader(value = Constants.HEADER_CLIENT, defaultValue = Constants.CLIENT) String client) throws InterruptedException {
        Event<FintResource> event = new Event<>(orgId, Constants.SOURCE, Actions.GET_ALL_ADDRESSES, client);
        fintEvents.sendDownstream(orgId, event);

        RBlockingQueue<Event<FintResource>> tempQueue = fintEvents.getTempQueue("test-consumer-" + event.getCorrId());
        Event<FintResource> receivedEvent = tempQueue.poll(30, TimeUnit.SECONDS);
        List<FintResource<Address>> fintResources = EventUtil.convertEventData(receivedEvent, new TypeReference<List<FintResource<Address>>>() {
        });

        return ResponseEntity.ok(fintResources);
    }

    @FintRelations
    @GetMapping("/{id}")
    public ResponseEntity getAddress(@PathVariable String id,
                                    @RequestHeader(value = Constants.HEADER_ORGID, defaultValue = Constants.ORGID) String orgId,
                                     @RequestHeader(value = Constants.HEADER_CLIENT, defaultValue = Constants.CLIENT) String client) throws InterruptedException {
        Event<String> event = new Event<>(orgId, Constants.SOURCE, Actions.GET_ADDRESS, client);
        event.setData(Lists.newArrayList(id));
        fintEvents.sendDownstream(orgId, event);

        RBlockingQueue<Event<FintResource>> tempQueue = fintEvents.getTempQueue("test-consumer-" + event.getCorrId());
        Event<FintResource> receivedEvent = tempQueue.poll(30, TimeUnit.SECONDS);
        List<FintResource<Address>> fintResources = EventUtil.convertEventData(receivedEvent, new TypeReference<List<FintResource<Address>>>() {
        });

        return ResponseEntity.ok(fintResources.get(0));
    }
}
