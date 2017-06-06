package no.fint.provider.events.admin;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class AdminService {

    @Getter
    private Map<String, Long> orgIds = new ConcurrentHashMap<>();

    public Long getTimestamp(String orgId) {
        return orgIds.get(orgId);
    }

    public boolean isRegistered(String orgId) {
        return orgIds.containsKey(orgId);
    }

    public void register(String orgId) {
        if (isRegistered(orgId)) {
            log.warn("OrgId {} is already registered, skipping new registration", orgId);
        } else {
            orgIds.put(orgId, System.currentTimeMillis());
        }
    }
}
