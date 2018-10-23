package no.fint.provider.events.testmode

import no.fint.provider.events.testmode.adapter.TestModeAdapter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Specification

@SpringBootTest
class TestModeDisabledSpec extends Specification {

    @Autowired(required = false)
    private TestModeController controller

    @Autowired(required = false)
    private TestModeAdapter adapter

    def "Test mode components are disabled by default"() {
        expect:
        !controller
        !adapter
    }
}
