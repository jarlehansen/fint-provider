package no.fint.provider.sse

import spock.lang.Specification

class FintSseEmitterSpec extends Specification {

    def "Create new FintSseEmitter"() {
        when:
        def fintSseEmitter = new FintSseEmitter('123', 'client', 123)

        then:
        fintSseEmitter.id == '123'
        fintSseEmitter.registered != null
    }
}
