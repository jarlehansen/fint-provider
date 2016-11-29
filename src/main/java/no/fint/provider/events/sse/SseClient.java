package no.fint.provider.events.sse;

import lombok.Data;

@Data
public class SseClient {
    private String orgId;
    private long registered;
}
