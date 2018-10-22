package no.fint.provider.events.testMode.consumer

import no.fint.provider.events.testMode.adapter.TestModeAdapter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Specification

@SpringBootTest
class TestModeControllerSpec extends Specification {

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
