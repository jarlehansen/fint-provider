package no.fint.provider.events.sse

import no.fint.event.model.Event
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import spock.lang.Specification

import java.util.concurrent.ConcurrentHashMap

class SseServiceSpec extends Specification {
    private SseService sseService

    void setup() {
        sseService = new SseService(maxNumberOfEmitters: 5)
    }

    def "Return SseEmitter when subscribing with new orgId"() {
        when:
        def emitter = sseService.subscribe('123', 'hfk.no')

        then:
        emitter != null
    }


    def "Return new SseEmitter when subscribing with already registered orgId"() {
        when:
        def emitter1 = sseService.subscribe('123', 'hfk.no')
        def emitter2 = sseService.subscribe('234', 'hfk.no')

        then:
        sseService.getSseClients().size() == 1
        emitter1 != emitter2
    }

    def "Send Event to registered emitter"() {
        given:
        def event = new Event('hfk.no', 'FK', 'GET_ALL_EMPLOYEES', 'test')
        sseService.subscribe('123', 'hfk.no')

        when:
        sseService.send(event)

        then:
        noExceptionThrown()
    }

    def "Remove registered emitter on exception when trying to send message"() {
        given:
        def emitter = Mock(FintSseEmitter)
        def emitters = FintSseEmitters.with(5)
        emitters.add(emitter)
        def clients = ['hfk.no': emitters] as ConcurrentHashMap
        sseService = new SseService(maxNumberOfEmitters: 5, clients: clients)

        when:
        sseService.send(new Event(orgId: 'hfk.no'))

        then:
        1 * emitter.send(_ as SseEmitter.SseEventBuilder) >> { throw new IllegalStateException('Test exception') }
        sseService.getSseClients().get('hfk.no').size() == 0
    }

    def "Remove all registered SSE clients"() {
        given:
        def clients = ['rogfk.no': FintSseEmitters.with(5)] as ConcurrentHashMap
        sseService = new SseService(maxNumberOfEmitters: 5, clients: clients)

        when:
        sseService.removeAll()
        def registeredClients = sseService.getSseClients()

        then:
        registeredClients.size() == 0
    }
}
