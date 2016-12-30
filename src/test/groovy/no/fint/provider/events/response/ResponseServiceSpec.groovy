package no.fint.provider.events.response

import no.fint.audit.FintAuditService
import no.fint.event.model.Event
import no.fint.events.FintEvents
import no.fint.provider.eventstate.EventState
import no.fint.provider.eventstate.EventStateService
import spock.lang.Specification

class ResponseServiceSpec extends Specification {
    private ResponseService responseService
    private EventStateService eventStateService
    private FintEvents fintEvents
    private FintAuditService auditService

    void setup() {
        fintEvents = Mock(FintEvents)
        eventStateService = Mock(EventStateService)
        auditService = Mock(FintAuditService)
        responseService = new ResponseService(fintEvents: fintEvents, eventStateService: eventStateService, fintAuditService: auditService)
    }

    def "Handle adapter response for existing event"() {
        given:
        def corrId = '12345'
        def event = new Event(corrId: corrId, orgId: 'orgId')

        when:
        def handled = responseService.handleAdapterResponse(event)

        then:
        1 * eventStateService.get(corrId) >> Optional.of(new EventState(event: event))
        2 * auditService.audit(_ as Event, _ as Boolean)
        1 * fintEvents.sendUpstream(_ as String, _ as Event)
        1 * eventStateService.clear(event)
        handled
    }

    def "Handle adapter response for direct reply-to existing event"() {
        given:
        def corrId = '12345'
        def event = new Event(corrId: corrId, orgId: 'orgId')

        when:
        def handled = responseService.handleAdapterResponse(event)

        then:
        1 * eventStateService.get(corrId) >> Optional.of(new EventState(event: event, replyTo: 'test123'))
        2 * auditService.audit(_ as Event, _ as Boolean)
        1 * fintEvents.reply(_ as String, _ as Event)
        1 * eventStateService.clear(event)
        handled
    }

    def "Handle adapter response for not existing event"() {
        given:
        def corrId = '12345'
        def event = new Event(corrId: corrId, orgId: 'orgId')

        when:
        def handled = responseService.handleAdapterResponse(event)

        then:
        1 * eventStateService.get(corrId) >> Optional.empty()
        !handled
    }
}
