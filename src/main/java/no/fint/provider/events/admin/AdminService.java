package no.fint.provider.events.admin;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import no.fint.event.model.DefaultActions;
import no.fint.event.model.Event;
import no.fint.events.FintEvents;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class AdminService {

    @Autowired
    private FintEvents fintEvents;

    @Getter
    private Map<String, Long> orgIds = new ConcurrentHashMap<>();

    public Long getTimestamp(String orgId) {
        return orgIds.get(orgId);
    }

    // TODO This is not safe - a restarted consumer needs to get re-registered.
    @Deprecated
    public boolean isRegistered(String orgId) {
        return orgIds.containsKey(orgId);
    }

    // TODO Validation towards fint-admin-portal for enabled orgIds.
    public boolean register(String orgId, String client) {
        if (isRegistered(orgId)) {
            log.warn("OrgId {} is already registered, skipping new registration", orgId);
            return true;
        } else {
            Event e = new Event(orgId, "provider", DefaultActions.REGISTER_ORG_ID, client);
            fintEvents.sendUpstream(e);
            orgIds.put(orgId, System.currentTimeMillis());
            return true;
        }
    }
}
