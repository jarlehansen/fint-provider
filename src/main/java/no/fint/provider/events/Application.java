package no.fint.provider.events;

import com.github.springfox.loader.EnableSpringfox;
import io.swagger.annotations.Extension;
import io.swagger.annotations.ExtensionProperty;
import io.swagger.annotations.Info;
import no.fint.audit.EnableFintAudit;
import no.fint.events.annotations.EnableFintEvents;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableFintAudit
@EnableFintEvents
@EnableSpringfox(@Info(title = "Provider API", version = "${fint.version}",
        extensions = {@Extension(name = "x-logo",
                properties = {@ExtensionProperty(name = "url", value = "/images/logo.png")}
        )}
))
@EnableScheduling
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
