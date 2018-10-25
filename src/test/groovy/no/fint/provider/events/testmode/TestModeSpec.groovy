package no.fint.provider.events.testmode

import no.fint.provider.events.Application
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.embedded.LocalServerPort
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.test.context.ActiveProfiles
import spock.lang.Requires
import spock.lang.Specification

import java.util.concurrent.TimeUnit

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, classes = Application)
@ActiveProfiles('local')
@Requires({ Boolean.valueOf(sys['integration-test']) })
class TestModeSpec extends Specification {

    @LocalServerPort
    private int port

    @Value('${server.context-path:/}')
    private String contextPath

    @Autowired
    private TestRestTemplate restTemplate

    def 'Loopback events'() {
        when:
        TimeUnit.SECONDS.sleep(10)
        def response1 = restTemplate.postForEntity('http://localhost:{port}/{context}/test-mode-consumer?numberOfEvents={events}', null, Void, port, contextPath, 10)

        then:
        response1.statusCode.is2xxSuccessful()

        when:
        TimeUnit.SECONDS.sleep(10)
        def response2 = restTemplate.getForEntity('http://localhost:{port}/{context}/test-mode-consumer', Map, port, contextPath)

        then:
        response2.statusCode.is2xxSuccessful()
        response2.body['numberOfEvents'] == 10
    }
}
