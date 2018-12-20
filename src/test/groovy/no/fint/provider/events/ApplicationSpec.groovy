package no.fint.provider.events

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContext
import spock.lang.Ignore
import spock.lang.Specification

@Ignore
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, classes = Application, properties = ['fint.version=DUMMY'])
class ApplicationSpec extends Specification {

    @Autowired
    ApplicationContext applicationContext

    def 'Application Context is valid'() {
        expect:
        applicationContext != null
    }
}
