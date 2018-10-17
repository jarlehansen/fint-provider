package no.fint.provider.events;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class ProviderProps {

    @Value("${fint.provider.ttl-status:2}")
    private int statusTtl;

    @Value("${fint.provider.ttl-response:15}")
    private int responseTtl;

    @Value("${fint.provider.max-number-of-emitters:50}")
    private int maxNumberOfEmitters;

    @Value("${fint.provider.event-state.list-name:current-corrids}")
    private String key;

    @Value("${fint.provider.sse-timeout-minutes:17}")
    private int sseTimeoutMinutes;

    @Value("${fint.provider.trace.downstream:false}")
    private boolean traceDownstream;

    @Value("${fint.provider.trace.response:false}")
    private boolean traceResponse;

}
