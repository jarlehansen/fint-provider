package no.fint.provider.events.testMode.adapter;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import no.fint.event.model.Event;
import no.fint.event.model.HeaderConstants;
import no.fint.event.model.Status;
import no.fint.event.model.health.Health;
import no.fint.event.model.health.HealthStatus;
import no.fint.provider.events.testMode.TestModeConstants;
import no.fint.sse.AbstractEventListener;
import no.fint.sse.FintSse;
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
import java.time.Instant;

@Slf4j
@ConditionalOnProperty(name = "fint.provider.test-mode", havingValue = "true")
@Component
public class TestModeAdapter extends AbstractEventListener {
    private FintSse fintSse;
    private RestTemplate restTemplate = new RestTemplate();

    @PostConstruct
    public void init() {
        log.info("Test-mode enabled, starting sse adapter");
        fintSse = new FintSse("http://localhost:8080/provider/sse/%s");
        fintSse.connect(this, ImmutableMap.of(
                HeaderConstants.ORG_ID, TestModeConstants.ORGID,
                HeaderConstants.CLIENT, TestModeConstants.CLIENT
        ));
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
        } else if (TestModeConstants.ACTION.equals(event.getAction())) {
            Event<String> responseEvent = new Event<>(event);
            responseEvent.setStatus(Status.ADAPTER_ACCEPTED);
            postStatus(responseEvent);

            responseEvent.addData(String.format("Message from test-adapter: %s", Instant.now().toString()));
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
}
