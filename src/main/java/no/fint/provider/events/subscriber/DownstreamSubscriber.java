package no.fint.provider.events.subscriber;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.extern.slf4j.Slf4j;
import no.fint.audit.FintAuditService;
import no.fint.event.model.Event;
import no.fint.event.model.Status;
import no.fint.event.model.health.Health;
import no.fint.event.model.health.HealthStatus;
import no.fint.events.FintEventListener;
import no.fint.provider.events.Constants;
import no.fint.provider.events.ProviderProps;
import no.fint.provider.events.eventstate.EventStateService;
import no.fint.provider.events.sse.SseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

@Slf4j
@Component
public class DownstreamSubscriber implements FintEventListener {

    @Value("${fint.provider.tracing:false}")
    private boolean tracing;

    @Autowired
    private SseService sseService;

    @Autowired
    private EventStateService eventStateService;

    @Autowired
    private FintAuditService fintAuditService;

    @Autowired
    private ProviderProps providerProps;

    private Path traceFile;
    private ObjectMapper objectMapper;

    @PostConstruct
    public void init() throws IOException {
        if (tracing) {
            traceFile = Files.createTempFile("trace", ".json");
            Files.write(traceFile, "[\n{}".getBytes());
            objectMapper = new ObjectMapper().disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
            log.info("Tracing inbound events to {}", traceFile.toAbsolutePath());
        }
    }

    @PreDestroy
    public void shutdown() throws IOException {
        if (tracing)
            try (OutputStream os = Files.newOutputStream(traceFile, StandardOpenOption.APPEND, StandardOpenOption.SYNC)) {
                os.write("\n]\n".getBytes());
            }
    }

    @Override
    public void accept(Event event) {
        log.debug("Event received: {}", event);
        if (tracing) {
            try (OutputStream os = Files.newOutputStream(traceFile, StandardOpenOption.APPEND, StandardOpenOption.SYNC)){
                os.write(",\n".getBytes());
                objectMapper.writeValue(os, event);
            } catch (IOException e) {
                log.info("Unable to trace event", e);
            }
        }
        if (event.isHealthCheck()) {
            event.addObject(new Health(Constants.COMPONENT, HealthStatus.RECEIVED_IN_PROVIDER_FROM_CONSUMER));
        }

        sseService.send(event);
        fintAuditService.audit(event, Status.DELIVERED_TO_ADAPTER);

        if (!event.isHealthCheck()) {
            eventStateService.add(event, providerProps.getStatusTtl());
        }
    }
}
