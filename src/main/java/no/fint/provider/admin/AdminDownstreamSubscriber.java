package no.fint.provider.admin;

import lombok.extern.slf4j.Slf4j;
import no.fint.event.model.DefaultActions;
import no.fint.event.model.Event;
import no.fint.events.FintEventListener;
import no.fint.events.FintEvents;
import no.fint.provider.Constants;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Slf4j
@Component
public class AdminDownstreamSubscriber implements FintEventListener {

    @Autowired
    private FintEvents fintEvents;

    @Autowired
    private AdminService adminService;

    @PostConstruct
    public void init() {
        fintEvents.registerDownstreamSystemListener( this);
    }

    @Override
    public void accept(Event event) {
        if (event.isRegisterOrgId()) {
            if (StringUtils.isEmpty(event.getOrgId())) {
                log.info("Bootstrapping consumer with registered organizations: {}", adminService.getOrgIds().keySet());
                adminService
                        .getOrgIds()
                        .keySet()
                        .stream()
                        .map(orgId -> new Event(orgId, Constants.COMPONENT, DefaultActions.REGISTER_ORG_ID, Constants.COMPONENT))
                        .forEach(fintEvents::sendUpstream);
            } else {
                adminService.register(event.getOrgId(), event.getClient());
            }
        } else {
            log.error("Cannot process system event {}", event.getAction());
        }
    }
}
