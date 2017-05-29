package no.fint.adapter;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import no.fint.Actions;
import no.fint.Constants;
import no.fint.event.model.Event;
import no.fint.event.model.EventUtil;
import no.fint.event.model.Health;
import no.fint.event.model.Status;
import no.fint.model.relation.FintResource;
import no.fint.sse.FintSse;
import org.glassfish.jersey.media.sse.EventListener;
import org.glassfish.jersey.media.sse.InboundEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.UUID;

@Slf4j
@ConditionalOnProperty(name = "adapter-enabled", havingValue = "true", matchIfMissing = true)
@Component
public class Adapter implements EventListener {
    private FintSse fintSse;

    @Autowired
    private Resources resources;

    @Autowired
    private RestTemplate restTemplate;

    @PostConstruct
    public void init() {
        log.info("Starting adapter");
        String sseUrl = String.format("http://localhost:8080/provider/sse/%s", UUID.randomUUID().toString());
        fintSse = new FintSse(sseUrl);
        fintSse.connect(this, ImmutableMap.of(Constants.HEADER_ORGID, Constants.ORGID));
    }

    @Scheduled(fixedRate = 5000L)
    public void checkSseConnection() {
        boolean connected = fintSse.verifyConnection();
        if (!connected) {
            log.info("Reconnecting SSE client");
        }
    }

    @PreDestroy
    public void shutdown() {
        fintSse.close();
    }

    @Override
    public void onEvent(InboundEvent inboundEvent) {
        String jsonEvent = inboundEvent.readData(String.class);
        Event event = EventUtil.toEvent(jsonEvent);
        log.info(event.toString());

        if (event.getAction().equals(Actions.HEALTH)) {
            Event<Health> healthCheckEvent = new Event<>(event);
            healthCheckEvent.setStatus(Status.TEMP_UPSTREAM_QUEUE);
            healthCheckEvent.addData(new Health("test-adapter", "Response from test adapter"));
            postResponse(healthCheckEvent);
        } else {
            Event<FintResource> responseEvent = new Event<>(event);
            responseEvent.setStatus(Status.PROVIDER_ACCEPTED);
            postStatus(responseEvent);

            if (event.getAction().equals(Actions.GET_ALL_PERSONS)) {
                responseEvent.setData(resources.createPersonList());
            } else if (event.getAction().equals(Actions.GET_ALL_ADDRESSES)) {
                responseEvent.setData(resources.createAddressList());
            } else if (event.getAction().equals(Actions.GET_ADDRESS)) {
                responseEvent.setData(resources.createAddress(event.getQuery()));
            } else if (event.getAction().equals(Actions.GET_PERSON)) {
                responseEvent.setData(resources.createPerson(event.getQuery()));
            }
            postResponse(responseEvent);
        }
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
