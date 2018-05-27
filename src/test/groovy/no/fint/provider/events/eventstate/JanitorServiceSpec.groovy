package no.fint.provider.events.eventstate

import no.fint.audit.FintAuditService
import no.fint.event.model.Event
import no.fint.events.FintEvents
import spock.lang.Specification

class JanitorServiceSpec extends Specification {
    private EventStateService eventStateService
    private FintAuditService fintAuditService
    private JanitorService janitorService
    private FintEvents fintEvents

    void setup() {
        eventStateService = Mock(EventStateService)
        fintAuditService = Mock(FintAuditService)
        fintEvents = Mock(FintEvents)
        janitorService = new JanitorService(eventStateService: eventStateService, fintAuditService: fintAuditService, fintEvents: fintEvents)
    }

    def "Remove expired event states"() {
        given:
        def event = new Event(orgId: 'rogfk.no')
        def expiredEventState = new EventState(event, -10)
        def notExpiredEventState = new EventState(event, 5000)

        when:
        janitorService.cleanUpEventStates()

        then:
        1 * eventStateService.getEventStates() >> [expiredEventState, notExpiredEventState]
        1 * eventStateService.remove(_ as Event)
        1 * fintAuditService.audit(_ as Event)
        1 * fintEvents.sendUpstream(_ as Event)
    }
}
