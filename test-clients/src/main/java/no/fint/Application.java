package no.fint;

import com.github.springfox.loader.EnableSpringfox;
import io.swagger.annotations.Info;
import no.fint.events.annotations.EnableFintEvents;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableFintEvents
@EnableSpringfox(@Info(title = "test-clients", version = "0.0.1-SNAPSHOT"))
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
