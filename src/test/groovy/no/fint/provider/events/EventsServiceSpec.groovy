package no.fint.provider.events

import no.fint.audit.FintAuditService
import no.fint.event.model.Event
import no.fint.event.model.Status
import no.fint.provider.ProviderProps
import no.fint.provider.eventstate.EventStateService
import spock.lang.Specification

class EventsServiceSpec extends Specification {

    private EventStateService eventStateService
    private FintAuditService fintAuditService
    private ProviderProps providerProps

    def setup() {
        eventStateService = Mock()
        fintAuditService = Mock()
        providerProps = Mock()
    }

    def 'Get events for registered orgId'() {
        given:
        def service = new EventsService(5, eventStateService, fintAuditService, providerProps)
        def event = new Event(orgId: 'test.org')

        when:
        service.register('test.org')
        service.accept(event)
        def result = service.drainEvents('test.org')
        service.unregister('test.org')

        then:
        result.size() == 1
        1 * eventStateService.add(event, 120)
        1 * providerProps.statusTtl >> 120
        1 * fintAuditService.audit(event, Status.DELIVERED_TO_ADAPTER)
    }

    def 'No events for unregistered orgId'() {
        given:
        def service = new EventsService(5, eventStateService, fintAuditService, providerProps)
        def event = new Event(orgId: 'test.org')

        when:
        service.accept(event)
        def result = service.drainEvents('test.org')

        then:
        result.isEmpty()

    }
}
