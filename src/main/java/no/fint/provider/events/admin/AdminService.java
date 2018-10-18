package no.fint.provider.events.admin;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import no.fint.event.model.DefaultActions;
import no.fint.event.model.Event;
import no.fint.events.FintEvents;
import no.fint.provider.events.Constants;
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

    private RestTemplate restTemplate = new RestTemplate();

    @Value("${fint.provider.assets.endpoint:}")
    private String assetsEndpoint;

    @Value("${fint.events.orgIds:}")
    private volatile String[] validAssets;

    @Scheduled(initialDelay = 1, fixedRateString = "${fint.provider.assets.rate:3600000}")
    public void refreshAssets() {
        if (StringUtils.isEmpty(assetsEndpoint))
            return;
        String[] assets = restTemplate.getForObject(assetsEndpoint, String[].class);
        if (ArrayUtils.isNotEmpty(assets)) {
            validAssets = assets;
            log.info("Valid assets: {}", Arrays.toString(validAssets));
        }
    }

    @Getter
    private Map<String, Long> orgIds = new ConcurrentHashMap<>();

    public Long getTimestamp(String orgId) {
        return orgIds.get(orgId);
    }

    public boolean isRegistered(String orgId) {
        return orgIds.containsKey(orgId);
    }

    public boolean register(String orgId, String client) {
        if (!isRegistered(orgId) && ArrayUtils.isNotEmpty(validAssets) && Stream.of(validAssets).noneMatch(orgId::equals)) {
            log.warn("OrgId {} is not enabled!", orgId);
            return false;
        } else {
            Event e = new Event(orgId, Constants.COMPONENT, DefaultActions.REGISTER_ORG_ID, client);
            fintEvents.sendUpstream(e);
            orgIds.put(orgId, System.currentTimeMillis());
            return true;
        }
    }
}
