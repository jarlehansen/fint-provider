package no.fint.provider.events.eventstate

import no.fint.audit.FintAuditService
import no.fint.event.model.Event
import spock.lang.Specification

class JanitorServiceSpec extends Specification {
    private EventStateService eventStateService
    private FintAuditService fintAuditService
    private JanitorService janitorService

    void setup() {
        eventStateService = Mock(EventStateService)
        fintAuditService = Mock(FintAuditService)
        janitorService = new JanitorService(eventStateService: eventStateService, fintAuditService: fintAuditService)
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
    }
}
