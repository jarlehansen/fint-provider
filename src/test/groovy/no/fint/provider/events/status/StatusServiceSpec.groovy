package no.fint.provider.events.status

import no.fint.audit.FintAuditService
import no.fint.event.model.Event
import no.fint.event.model.Status
import no.fint.events.FintEvents
import no.fint.provider.events.eventstate.EventStateService
import no.fint.provider.events.exceptions.UnknownEventException
import spock.lang.Ignore
import spock.lang.Specification

@Ignore
class StatusServiceSpec extends Specification {
    private StatusService statusService
    private EventStateService eventStateService
    private FintAuditService fintAuditService
    private FintEvents fintEvents

    void setup() {
        eventStateService = Mock(EventStateService)
        fintAuditService = Mock(FintAuditService)
        fintEvents = Mock(FintEvents)

        statusService = new StatusService(
                eventStateService: eventStateService,
                fintAuditService: fintAuditService,
                fintEvents: fintEvents)
    }

    def "Handle event state for existing event with status PROVIDER_REJECTED"() {
        given:
        def event = new Event(orgId: 'rogfk.no', status: Status.PROVIDER_REJECTED)

        when:
        statusService.updateEventState(event)

        then:
        1 * eventStateService.exists(event) >> true
        2 * fintAuditService.audit(_ as Event, true)
        1 * fintEvents.sendUpstream(_ as String, _ as Event)
        1 * eventStateService.remove(_ as Event)
    }

    def "Handle non-existing event state"() {
        when:
        statusService.updateEventState(new Event())

        then:
        1 * eventStateService.exists(_ as Event) >> false
        1 * fintAuditService.audit(_ as Event, _ as Boolean)
        thrown(UnknownEventException)
    }
}
