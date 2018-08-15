package no.fint.provider.events.response;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.extern.slf4j.Slf4j;
import no.fint.audit.FintAuditService;
import no.fint.event.model.Event;
import no.fint.event.model.Status;
import no.fint.events.FintEvents;
import no.fint.provider.events.eventstate.EventState;
import no.fint.provider.events.eventstate.EventStateService;
import no.fint.provider.events.exceptions.UnknownEventException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class ResponseService {

    @Value("${fint.provider.tracing:false}")
    private boolean tracing;

    @Autowired
    private EventStateService eventStateService;

    @Autowired
    private FintAuditService fintAuditService;

    @Autowired
    private FintEvents fintEvents;

    private Path traceFile;
    private ObjectMapper objectMapper;

    @PostConstruct
    public void init() throws IOException {
        if (tracing) {
            traceFile = Files.createTempFile("response", ".json");
            Files.write(traceFile, "[\n{}".getBytes());
            objectMapper = new ObjectMapper().disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
            log.info("Tracing response events to {}", traceFile.toAbsolutePath());
        }
    }

    @PreDestroy
    public void shutdown() throws IOException {
        if (tracing)
            try (OutputStream os = Files.newOutputStream(traceFile, StandardOpenOption.APPEND, StandardOpenOption.SYNC)) {
                os.write("\n]\n".getBytes());
            }
    }

    public void handleAdapterResponse(Event event) {
        log.debug("{}: Response for {} from {} status {} with {} elements.",
                event.getCorrId(), event.getAction(), event.getOrgId(), event.getStatus(),
                Optional.ofNullable(event.getData()).map(List::size).orElse(0));
        if (tracing) {
            try (OutputStream os = Files.newOutputStream(traceFile, StandardOpenOption.APPEND, StandardOpenOption.SYNC)){
                os.write(",\n".getBytes());
                objectMapper.writeValue(os, event);
            } catch (IOException e) {
                log.info("Unable to trace event", e);
            }
        }
        if (event.isHealthCheck()) {
            event.setStatus(Status.UPSTREAM_QUEUE);
            fintEvents.sendUpstream(event);
        } else {
            Optional<EventState> state = eventStateService.remove(event);
            if (state.isPresent()) {
                fintAuditService.audit(event, Status.ADAPTER_RESPONSE);
                event.setStatus(Status.UPSTREAM_QUEUE);
                fintEvents.sendUpstream(event);
                fintAuditService.audit(event, Status.UPSTREAM_QUEUE);
            } else {
                log.error("EventState with corrId {} was not found. Either the Event has expired or the provider does not recognize the corrId. {}", event.getCorrId(), event);
                throw new UnknownEventException();
            }
        }
    }
}
