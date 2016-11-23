package no.fint.provider.events.poll

import com.fasterxml.jackson.databind.ObjectMapper
import no.fint.audit.FintAuditService
import no.fint.event.model.Event
import no.fint.events.FintEvents
import no.fint.provider.eventstate.EventStateService
import org.springframework.amqp.core.Message
import spock.lang.Specification

class PollServiceSpec extends Specification {
    private PollService pollService
    private FintEvents fintEvents
    private EventStateService eventStateService
    private FintAuditService fintAuditService

    void setup() {
        fintEvents = Mock(FintEvents)
        eventStateService = Mock(EventStateService)
        fintAuditService = Mock(FintAuditService)
        pollService = new PollService(events: fintEvents, eventStateService: eventStateService, fintAuditService: fintAuditService, objectMapper: new ObjectMapper())
    }

    def "Return empty no message is on the queue"() {
        when:
        def event = pollService.readEvent('hfk.no')

        then:
        1 * fintEvents.readDownstreamMessage('hfk.no') >> Optional.empty()
        !event.isPresent()
    }

    def "Return empty if message is not an Event"() {
        given:
        def message = new Message('test message'.bytes, null)

        when:
        def event = pollService.readEvent('hfk.no')

        then:
        1 * fintEvents.readDownstreamMessage('hfk.no') >> Optional.of(message)
        !event.isPresent()
    }

    def "Return Event object if message is on queue"() {
        given:
        def message = new Message(new ObjectMapper().writeValueAsBytes(new Event('hfk.no', 'test', 'test', 'test')), null)

        when:
        def event = pollService.readEvent('hfk.no')

        then:
        1 * fintEvents.readDownstreamMessage('hfk.no') >> Optional.of(message)
        1 * eventStateService.addEventState(_ as Event)
        1 * fintAuditService.audit(_ as Event, _ as Boolean)
        event.isPresent()
    }
}
