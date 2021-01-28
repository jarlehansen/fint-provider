package no.fint.provider.events

import no.fint.event.model.Event
import spock.lang.Specification

class EventsServiceSpec extends Specification {

    def 'Get events for registered orgId'() {
        given:
        def service = new EventsService(5)
        def event = new Event(orgId: 'test.org')

        when:
        service.register('test.org')
        service.add(event)
        def result = service.drainEvents('test.org')
        service.unregister('test.org')

        then:
        result.size() == 1
    }

    def 'No events for unregistered orgId'() {
        given:
        def service = new EventsService(5)
        def event = new Event(orgId: 'test.org')

        when:
        service.add(event)
        def result = service.drainEvents('test.org')

        then:
        result.isEmpty()

    }
}
