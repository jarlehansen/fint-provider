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
        def expiredTimestamp = System.currentTimeMillis() - 10
        def notExpiredTimestamp = System.currentTimeMillis() + 5000
        def expiredEventState = new EventState(corrId: '123', event: event, expires: expiredTimestamp)
        def notExpiredEventState = new EventState(corrId: '234', event: event, expires: notExpiredTimestamp)

        when:
        janitorService.cleanUpEventStates()

        then:
        1 * eventStateService.getEventStates() >> [expiredEventState, notExpiredEventState]
        1 * eventStateService.remove(_ as Event)
    }
}
