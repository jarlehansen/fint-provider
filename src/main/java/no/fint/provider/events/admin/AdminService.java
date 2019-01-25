package no.fint.provider.events.admin;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import no.fint.event.model.DefaultActions;
import no.fint.event.model.Event;
import no.fint.events.FintEvents;
import no.fint.provider.events.Constants;
import no.fint.provider.events.ProviderProps;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

@Slf4j
@Service
public class AdminService {

    @Autowired
    private FintEvents fintEvents;

    @Autowired
    private ProviderProps props;

    private RestTemplate restTemplate = new RestTemplate();

    @Value(ProviderProps.EVENTS_ORG_IDS)
    private volatile String[] validAssets;

    @Getter
    private Map<String, Long> orgIds = new ConcurrentHashMap<>();

    @Scheduled(initialDelay = 100, fixedRateString = ProviderProps.PROVIDER_ASSETS_RATE)
    public void refreshAssets() {
        if (StringUtils.isNotEmpty(props.getAssetsEndpoint())) {
            String[] assets = restTemplate.getForObject(props.getAssetsEndpoint(), String[].class);
            if (ArrayUtils.isNotEmpty(assets)) {
                validAssets = assets;
                log.info("Valid assets: {}", Arrays.toString(validAssets));
            }
        }
    }

    public Long getTimestamp(String orgId) {
        return orgIds.get(orgId);
    }

    public boolean isRegistered(String orgId) {
        return orgIds.containsKey(orgId);
    }

    public boolean register(String orgId, String client) {
        if (!isRegistered(orgId) && orgNotEnabled(orgId)) {
            log.warn("OrgId '{}' is not enabled!", orgId);
            return false;
        } else {
            Event e = new Event(orgId, Constants.COMPONENT, DefaultActions.REGISTER_ORG_ID, client);
            fintEvents.sendUpstream(e);
            orgIds.put(orgId, System.currentTimeMillis());
            return true;
        }
    }

    private boolean orgNotEnabled(String orgId) {
        return ArrayUtils.isNotEmpty(validAssets) && Stream.of(validAssets).noneMatch(orgId::equals);
    }
}
