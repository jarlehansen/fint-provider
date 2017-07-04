package no.fint.adapter;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import no.fint.Actions;
import no.fint.Constants;
import no.fint.event.model.DefaultActions;
import no.fint.event.model.Event;
import no.fint.event.model.HeaderConstants;
import no.fint.event.model.Status;
import no.fint.event.model.health.Health;
import no.fint.event.model.health.HealthStatus;
import no.fint.model.relation.FintResource;
import no.fint.sse.AbstractEventListener;
import no.fint.sse.FintSse;
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

@Slf4j
@ConditionalOnProperty(name = "adapter-enabled", havingValue = "true", matchIfMissing = true)
@Component
public class Adapter extends AbstractEventListener {
    private FintSse fintSse;

    @Autowired
    private Resources resources;

    @Autowired
    private RestTemplate restTemplate;

    @PostConstruct
    public void init() {
        log.info("Starting adapter");
        fintSse = new FintSse("http://localhost:8080/provider/sse/%s");
        fintSse.connect(this, ImmutableMap.of(HeaderConstants.ORG_ID, Constants.ORGID), DefaultActions.HEALTH);
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
    public void onEvent(Event event) {
        log.info(event.toString());

        if (event.isHealthCheck()) {
            Event<Health> healthCheckEvent = new Event<>(event);
            healthCheckEvent.setStatus(Status.TEMP_UPSTREAM_QUEUE);
            healthCheckEvent.addData(new Health("test-adapter", HealthStatus.APPLICATION_HEALTHY));
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
        headers.put(HeaderConstants.ORG_ID, Lists.newArrayList(event.getOrgId()));
        ResponseEntity<Void> response = restTemplate.exchange("http://localhost:8080/provider/status", HttpMethod.POST, new HttpEntity<>(event, headers), Void.class);
        log.info("Provider POST response: {}", response.getStatusCode());
    }

    private void postResponse(Event event) {
        HttpHeaders headers = new HttpHeaders();
        headers.put(HeaderConstants.ORG_ID, Lists.newArrayList(event.getOrgId()));
        ResponseEntity<Void> response = restTemplate.exchange("http://localhost:8080/provider/response", HttpMethod.POST, new HttpEntity<>(event, headers), Void.class);
        log.info("Provider POST response: {}", response.getStatusCode());
    }

    public void registerOrgId(String orgId) {
        fintSse.connect(this, ImmutableMap.of(HeaderConstants.ORG_ID, orgId));
    }
}
