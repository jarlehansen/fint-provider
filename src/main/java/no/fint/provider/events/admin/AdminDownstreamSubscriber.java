package no.fint.provider.events.admin;

import lombok.extern.slf4j.Slf4j;
import no.fint.event.model.Event;
import no.fint.events.FintEventListener;
import no.fint.events.FintEvents;
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
            adminService.register(event.getOrgId(), event.getClient());
        } else {
            log.error("Cannot process system event {}", event.getAction());
        }
    }
}
