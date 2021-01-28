package no.fint.provider.testmode

import no.fint.provider.testmode.adapter.TestModeAdapter
import no.fint.provider.testmode.consumer.TestModeController
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Ignore
import spock.lang.IgnoreIf
import spock.lang.Specification

@SpringBootTest
@IgnoreIf({ TestModeConditional.integrationTest() })
class TestModeDisabledSpec extends Specification {

    @Autowired(required = false)
    private TestModeController controller

    @Autowired(required = false)
    private TestModeAdapter adapter

    @Ignore
    def "Test mode components are disabled by default"() {
        expect:
        !controller
        !adapter
    }
}
