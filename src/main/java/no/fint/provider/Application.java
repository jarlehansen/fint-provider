package no.fint.provider;

import com.github.springfox.loader.EnableSpringfox;
import no.fint.audit.EnableFintAudit;
import no.fint.event.model.Event;
import no.fint.events.EnableFintEvents;
import no.fint.events.FintEvents;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.annotation.PostConstruct;

@EnableFintAudit
@EnableFintEvents
@EnableSpringfox
@EnableScheduling
@SpringBootApplication
public class Application {

    @Autowired
    private FintEvents fintEvents;

    @PostConstruct
    public void init() {
        fintEvents.setDefaultType(Event.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
