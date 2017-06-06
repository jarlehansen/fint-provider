package no.fint.provider.events.admin

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
        def adminService = new AdminService()

        when:
        adminService.register('123')
        adminService.register('123')

        then:
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
