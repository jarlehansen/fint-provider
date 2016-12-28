package no.fint.provider.events.sse;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Data
@EqualsAndHashCode(callSuper = true)
public class FintSseEmitter extends SseEmitter {
    private String id;

    public FintSseEmitter() {
    }

    public FintSseEmitter(String id, long timeout) {
        super(timeout);
        this.id = id;
    }
}
