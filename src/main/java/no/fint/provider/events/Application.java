package no.fint.provider.events;

import com.github.springfox.loader.EnableSpringfox;
import io.swagger.annotations.*;
import no.fint.audit.EnableFintAudit;
import no.fint.events.annotations.EnableFintEvents;
import no.fint.springfox.EnableSpringfoxExtension;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableFintAudit
@EnableFintEvents
@EnableSpringfoxExtension
@EnableSpringfox(@Info(title = "Provider API", version = "${fint.version}",
        extensions = {@Extension(name = "x-logo",
                properties = {@ExtensionProperty(name = "url", value = "/images/logo.png")}
        )}
))
@SwaggerDefinition(externalDocs = @ExternalDocs(value = "Go to the API list", url = "/api"))
@EnableScheduling
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
