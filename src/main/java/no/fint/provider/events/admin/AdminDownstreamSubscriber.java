package no.fint.provider.events.admin;

import lombok.extern.slf4j.Slf4j;
import no.fint.event.model.Event;
import no.fint.events.FintEvents;
import no.fint.events.annotations.FintEventListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Slf4j
@Component
public class AdminDownstreamSubscriber {

    @Autowired
    private FintEvents fintEvents;

    @Autowired
    private AdminService adminService;

    @PostConstruct
    public void init() {
        fintEvents.registerDownstreamListener(AdminDownstreamSubscriber.class, "system");
    }

    @FintEventListener
    public void receive(Event event) {
        if (event.isRegisterOrgId()) {
            adminService.register(event.getOrgId());
        } else {
            log.error("Cannot process event action {} with system orgId", event.getAction());
        }
    }
}
