package no.fint.provider.events.admin

import no.fint.event.model.Event
import no.fint.events.FintEvents
import spock.lang.Specification

class AdminServiceSpec extends Specification {

    def "Get timestamp"() {
        given:
        def adminService = new AdminService(orgIds: ['123': 123456L])

        when:
        def timestamp = adminService.getTimestamp('123')

        then:
        timestamp == 123456L
    }

    def "Return false when checking if new orgId is registered"() {
        given:
        def adminService = new AdminService()

        when:
        def registered = adminService.isRegistered('123')

        then:
        !registered
    }

    def "Register new orgId once"() {
        given:
        def fintEvents = Mock(FintEvents)
        def adminService = new AdminService(fintEvents: fintEvents)

        when:
        adminService.register('123', 'spock')
        adminService.register('123', 'spock')

        then:
        1 * fintEvents.sendUpstream(_ as Event)
        adminService.isRegistered('123')
        adminService.getTimestamp('123') > 0
    }

    def "Get all registered orgIds"() {
        given:
        def adminService = new AdminService(orgIds: ['123': 123456L, '234': 123456L])

        when:
        def orgIds = adminService.getOrgIds()

        then:
        orgIds.size() == 2
    }
}
