package no.fint.provider.events.response

import no.fint.audit.FintAuditService
import no.fint.event.model.Event
import no.fint.events.FintEvents

import no.fint.provider.events.eventstate.EventStateService
import spock.lang.Ignore
import spock.lang.Specification

@Ignore
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
        2 * auditService.audit(_ as Event, _ as Boolean)
        1 * fintEvents.sendUpstream(_ as String, _ as Event)
        1 * eventStateService.remove(event)
        handled
    }

    def "Handle adapter response for direct reply-to existing event"() {
        given:
        def corrId = '12345'
        def event = new Event(corrId: corrId, orgId: 'orgId')

        when:
        def handled = responseService.handleAdapterResponse(event)

        then:
        2 * auditService.audit(_ as Event, _ as Boolean)
        1 * eventStateService.remove(event)
        handled
    }

    def "Handle adapter response for not existing event"() {
        given:
        def corrId = '12345'
        def event = new Event(corrId: corrId, orgId: 'orgId')

        when:
        def handled = responseService.handleAdapterResponse(event)

        then:
        !handled
    }
}
