package no.fint.provider;

import com.github.springfox.loader.EnableSpringfox;
import no.fint.audit.EnableFintAudit;
import no.fint.events.EnableFintEvents;
//import no.fint.events.local.LocalRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.annotation.PreDestroy;

@EnableFintAudit
@EnableFintEvents
@EnableSpringfox
@EnableScheduling
@SpringBootApplication
public class Application {

    @PreDestroy
    public void shutdown() {
        /*LocalRabbit.stop();*/
    }

    public static void main(String[] args) {
        //LocalRabbit.start();
        SpringApplication.run(Application.class, args);
    }
}
