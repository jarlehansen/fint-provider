package no.fint.provider;

import com.github.springfox.loader.EnableSpringfox;
import no.fint.audit.EnableFintAudit;
import no.fint.events.FintEvents;
import no.fint.events.annotations.EnableFintEvents;
import no.fint.provider.events.subscriber.DownstreamSubscriber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${fint.events.orgs:}")
    private String[] orgs;

    @PostConstruct
    public void init() {
        fintEvents.registerDownstreamListener(DownstreamSubscriber.class, orgs);
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
