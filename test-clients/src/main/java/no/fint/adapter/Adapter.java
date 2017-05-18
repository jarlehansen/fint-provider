package no.fint.adapter;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import no.fint.Actions;
import no.fint.Constants;
import no.fint.dto.Address;
import no.fint.dto.Person;
import no.fint.event.model.Event;
import no.fint.event.model.EventUtil;
import no.fint.event.model.Status;
import no.fint.model.relation.FintResource;
import no.fint.model.relation.Relation;
import no.fint.sse.SseHeaderProvider;
import no.fint.sse.SseHeaderSupportFeature;
import org.glassfish.jersey.media.sse.EventListener;
import org.glassfish.jersey.media.sse.EventSource;
import org.glassfish.jersey.media.sse.InboundEvent;
import org.glassfish.jersey.media.sse.SseFeature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
public class Adapter implements EventListener {
    private EventSource eventSource;

    @Autowired
    private RestTemplate restTemplate;

    @PostConstruct
    public void init() {
        eventSource = EventSource.target(getWebTarget()).build();
        eventSource.register(this);
        eventSource.open();
    }

    @PreDestroy
    public void shutdown() {
        eventSource.close();
    }

    private WebTarget getWebTarget() {
        Map<String, String> map = new HashMap<>();
        map.put(Constants.HEADER_ORGID, Constants.ORGID);
        SseHeaderProvider provider = () -> map;
        Client client = ClientBuilder.newBuilder()
                .register(SseFeature.class)
                .register(new SseHeaderSupportFeature(provider))
                .build();

        String sseUrl = String.format("http://localhost:8080/provider/sse/%s", UUID.randomUUID().toString());
        return client.target(sseUrl);
    }

    @Override
    public void onEvent(InboundEvent inboundEvent) {
        String jsonEvent = inboundEvent.readData(String.class);
        Event event = EventUtil.toEvent(jsonEvent);
        log.info(event.toString());

        if (event.getAction().equals(Actions.HEALTH)) {
            event.setData(Lists.newArrayList("Response from test adapter"));
            postResponse(event);
        } else if (event.getAction().equals(Actions.GET_ALL_PERSONS)) {
            event.setStatus(Status.PROVIDER_ACCEPTED);
            postStatus(event);
            event.setData(createPersonList());
            postResponse(event);
        } else if (event.getAction().equals(Actions.GET_ALL_ADDRESSES)) {
            event.setStatus(Status.PROVIDER_ACCEPTED);
            postStatus(event);
            event.setData(createAddressList());
            postResponse(event);
        }
    }

    private List<FintResource> createPersonList() {
        Person person1 = new Person("1", "Mari");
        Relation relation1 = new Relation.Builder().with(Person.Relasjonsnavn.ADDRESS).forType(Person.class).field("address").value("1").build();
        FintResource<Person> resource1 = FintResource.with(person1).addRelasjoner(relation1);

        Person person2 = new Person("2", "Per");
        Relation relation2 = new Relation.Builder().with(Person.Relasjonsnavn.ADDRESS).forType(Person.class).field("address").value("2").build();
        FintResource<Person> resource2 = FintResource.with(person2).addRelasjoner(relation2);

        return Lists.newArrayList(resource1, resource2);
    }

    private List<FintResource> createAddressList() {
        Address address1 = new Address("1", "veien 1");
        Relation relation1 = new Relation.Builder().with(Address.Relasjonsnavn.PERSON).forType(Address.class).field("person").value("1").build();
        FintResource<Address> resource1 = FintResource.with(address1).addRelasjoner(relation1);

        Address address2 = new Address("2", "veien 2");
        Relation relation2 = new Relation.Builder().with(Address.Relasjonsnavn.PERSON).forType(Address.class).field("person").value("2").build();
        FintResource<Address> resource2 = FintResource.with(address2).addRelasjoner(relation2);

        return Lists.newArrayList(resource1, resource2);
    }

    private void postStatus(Event event) {
        HttpHeaders headers = new HttpHeaders();
        headers.put(Constants.HEADER_ORGID, Lists.newArrayList(Constants.ORGID));
        ResponseEntity<Void> response = restTemplate.exchange("http://localhost:8080/provider/status", HttpMethod.POST, new HttpEntity<>(event, headers), Void.class);
        log.info("Provider POST response: {}", response.getStatusCode());
    }

    private void postResponse(Event event) {
        HttpHeaders headers = new HttpHeaders();
        headers.put(Constants.HEADER_ORGID, Lists.newArrayList(Constants.ORGID));
        ResponseEntity<Void> response = restTemplate.exchange("http://localhost:8080/provider/response", HttpMethod.POST, new HttpEntity<>(event, headers), Void.class);
        log.info("Provider POST response: {}", response.getStatusCode());
    }
}
