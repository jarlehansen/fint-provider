package no.fint.provider.events.admin

import no.fint.event.model.DefaultActions
import no.fint.event.model.Event
import no.fint.events.FintEvents
import spock.lang.Specification

class AdminDownstreamSubscriberSpec extends Specification {
    private AdminService adminService
    private FintEvents fintEvents
    private AdminDownstreamSubscriber subscriber

    void setup() {
        adminService = Mock()
        fintEvents = Mock()
        subscriber = new AdminDownstreamSubscriber(adminService: adminService, fintEvents: fintEvents)
        subscriber.init()
    }

    def "Receive register orgId"() {
        given:
        def event = new Event('rogfk.no', 'test', DefaultActions.REGISTER_ORG_ID.name(), 'test')

        when:
        subscriber.accept(event)

        then:
        1 * adminService.register('rogfk.no', 'test')
    }

    def "Do not process event with unknown action"() {
        given:
        def event = new Event('rogfk.no', 'test', 'unknown', 'test')

        when:
        subscriber.accept(event)

        then:
        0 * adminService.register('rogfk.no', 'test')
    }

    def 'Boostrap orgid registration'() {
        given:
        def event = new Event('', '', DefaultActions.REGISTER_ORG_ID.name(), '')

        when:
        subscriber.accept(event)

        then:
        2 * adminService.getOrgIds() >> ['jalla.org' : 12, 'balla.com': 14]
        2 * fintEvents.sendUpstream(_ as Event)
    }
}
