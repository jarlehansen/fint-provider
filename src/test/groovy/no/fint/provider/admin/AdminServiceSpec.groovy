package no.fint.provider.admin

import no.fint.event.model.Event
import no.fint.events.FintEvents
import no.fint.provider.ProviderProps
import org.springframework.web.client.RestTemplate
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

    def "Register new orgId every time"() {
        given:
        def fintEvents = Mock(FintEvents)
        def adminService = new AdminService(fintEvents: fintEvents)

        when:
        adminService.register('123', 'spock')
        adminService.register('123', 'spock')

        then:
        2 * fintEvents.sendUpstream(_ as Event)
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

    def "Accept valid orgID and reject invalid orgID"() {
        given:
        def fintEvents = Mock(FintEvents)
        def adminService = new AdminService(validAssets: [ 'valid.org'], fintEvents: fintEvents)

        when:
        def valid = adminService.register('valid.org', 'spock')

        then:
        valid
        1 * fintEvents.sendUpstream(_ as Event)

        when:
        valid = adminService.register('invalid.org', 'spock')

        then:
        !valid
        0 * fintEvents.sendUpstream(_ as Event)
    }

    def "Refresh works if assets endpoint not configured"() {
        given:
        def adminService = new AdminService(props: Mock(ProviderProps))

        when:
        adminService.refreshAssets()

        then:
        noExceptionThrown()
    }

    def "Able to refresh assets using assets endpoint"() {
        given:
        def restTemplate = Mock(RestTemplate)
        def props = Mock(ProviderProps) {
            getAssetsEndpoint() >> 'http://fake.org'
        }
        def adminService = new AdminService(props: props, restTemplate: restTemplate)

        when:
        adminService.refreshAssets()

        then:
        1 * restTemplate.getForObject('http://fake.org', String[]) >> [ 'valid.org', 'valid.com' ].toArray(new String[2])
    }
}
