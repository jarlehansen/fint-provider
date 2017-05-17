package no.fint;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import no.fint.dto.Value;
import no.fint.event.model.Event;
import no.fint.event.model.EventUtil;
import no.fint.event.model.Status;
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
        } else if (event.getAction().equals(Actions.GET_ALL_VALUES)) {
            event.setStatus(Status.PROVIDER_ACCEPTED);
            postStatus(event);

            Value value1 = new Value("test value 1");
            Value value2 = new Value("test value 2");
            event.setData(Lists.newArrayList(value1, value2));
            postResponse(event);
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
