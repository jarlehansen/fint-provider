package no.fint.provider.events.poll

import no.fint.audit.FintAuditService
import no.fint.event.model.Event
import no.fint.events.FintEvents
import no.fint.provider.events.eventstate.EventStateService
import spock.lang.Specification

import java.util.concurrent.ArrayBlockingQueue

class PollServiceSpec extends Specification {
    private PollService pollService
    private FintEvents fintEvents
    private EventStateService eventStateService
    private FintAuditService fintAuditService

    void setup() {
        fintEvents = Mock(FintEvents)
        eventStateService = Mock(EventStateService)
        fintAuditService = Mock(FintAuditService)
        pollService = new PollService(events: fintEvents, eventStateService: eventStateService, fintAuditService: fintAuditService)
    }

    def "Return empty no message is on the queue"() {
        when:
        def event = pollService.readEvent('hfk.no')

        then:
        1 * fintEvents.getDownstream('hfk.no') >> new ArrayBlockingQueue<>(1)
        !event.isPresent()
    }

    def "Return Event object if message is on queue"() {
        given:
        def queue = new ArrayBlockingQueue(1)
        queue.put(new Event('hfk.no', 'test', 'test', 'test'))

        when:
        def event = pollService.readEvent('hfk.no')

        then:
        1 * fintEvents.getDownstream('hfk.no') >> queue
        1 * eventStateService.add(_ as Event, _ as Integer)
        event.isPresent()
    }
}
