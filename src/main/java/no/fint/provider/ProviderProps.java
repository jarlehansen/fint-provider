package no.fint.provider;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class ProviderProps {

    public static final String PROVIDER_ASSETS_RATE = "${fint.provider.assets.rate:3600000}";
    public static final String EVENTS_ORG_IDS = "${fint.events.orgIds:}";

    @Value("${fint.provider.ttl-status:2}")
    private int statusTtl;

    @Value("${fint.provider.ttl-response:15}")
    private int responseTtl;

    @Value("${fint.provider.max-number-of-emitters:50}")
    private int maxNumberOfEmitters;

    @Value("${fint.provider.event-state.list-name:current-corrids}")
    private String key;

    @Value("${fint.provider.event-state.hazelcast:true}")
    private boolean useHazelcastForEventState;

    @Value("${fint.provider.sse-timeout-minutes:17}")
    private int sseTimeoutMinutes;

    @Value("${fint.provider.assets.endpoint:}")
    private String assetsEndpoint;

    @Value("${server.context-path:/}")
    private String contextPath;

}
