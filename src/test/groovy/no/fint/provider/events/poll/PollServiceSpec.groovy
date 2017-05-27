package no.fint.provider.events.poll

import no.fint.audit.FintAuditService
import no.fint.event.model.Event
import no.fint.events.FintEvents
import no.fint.provider.events.eventstate.EventStateService
import spock.lang.Ignore
import spock.lang.Specification

@Ignore
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
        1 * fintEvents.readDownstream('hfk.no', Event) >> Optional.empty()
        !event.isPresent()
    }

    def "Return Event object if message is on queue"() {
        when:
        def event = pollService.readEvent('hfk.no')

        then:
        1 * fintEvents.readDownstream('hfk.no', Event) >> Optional.of(new Event('hfk.no', 'test', 'test', 'test'))
        1 * eventStateService.add(_ as Event)
        1 * fintAuditService.audit(_ as Event, _ as Boolean)
        event.isPresent()
    }
}
