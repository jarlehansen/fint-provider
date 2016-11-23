package no.fint.provider;

import no.fint.events.Events;
import no.fint.events.FintEvents;
import no.fint.provider.events.subscriber.DownstreamSubscriber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
public class MessagingConfig {

    @Autowired
    private FintEvents fintEvents;

    @Autowired
    private Events events;

    @PostConstruct
    public void init() {
       // events.registerListener("rogfk.no", "rogfk.no.downstream", DownstreamSubscriber.class);
        fintEvents.registerDownstreamListener("rogfk.no", DownstreamSubscriber.class);
    }

}
