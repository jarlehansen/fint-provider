package no.fint.provider.events.sse;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Getter
@AllArgsConstructor
public class SseClient {
    private String id;
    private String orgId;
    private SseEmitter emitter;

    public void close() {
        emitter.complete();
    }
}
