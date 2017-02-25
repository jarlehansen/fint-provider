package no.fint.provider.testutils;

import no.fint.provider.TestApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@ActiveProfiles("local")
@ContextConfiguration
@SpringBootTest(classes = TestApplication.class, properties = "APPLICATION_CONFIG_NAME=test")
public @interface LocalProfileTest {
}
