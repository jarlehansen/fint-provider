package no.fint.provider.events.testMode;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

@ConditionalOnProperty(name = "fint.provider.test-mode", havingValue = "true")
public @interface EnabledIfTestMode {
}
