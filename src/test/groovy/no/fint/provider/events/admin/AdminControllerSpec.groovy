package no.fint.provider.events.admin

import no.fint.audit.plugin.mongo.MongoAuditEvent
import no.fint.event.model.Event
import no.fint.events.FintEvents
import no.fint.provider.events.Constants
import no.fint.provider.events.subscriber.DownstreamSubscriber
import no.fint.test.utils.MockMvcSpecification
import org.redisson.api.RBlockingQueue
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.http.HttpHeaders
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

class AdminControllerSpec extends MockMvcSpecification {
    private AdminController controller
    private MongoTemplate mongoTemplate
    private FintEvents fintEvents
    private AdminService adminService
    private MockMvc mockMvc

    void setup() {
        adminService = Mock(AdminService)
        mongoTemplate = Mock(MongoTemplate)
        fintEvents = Mock(FintEvents)
        controller = new AdminController(mongoTemplate: mongoTemplate, fintEvents: fintEvents, adminService: adminService)
        mockMvc = standaloneSetup(controller)
    }

    def "GET all audit events"() {
        when:
        def response = mockMvc.perform(get('/admin/audit/events'))

        then:
        1 * mongoTemplate.findAll(MongoAuditEvent) >> [new MongoAuditEvent(new Event(orgId: 'rogfk.no'), true)]
        response.andExpect(status().isOk())
                .andExpect(jsonPath('$[0].orgId').value(equalTo('rogfk.no')))
    }

    def "DELETE temporary queues"() {
        when:
        def response = mockMvc.perform(delete('/admin/tempQueues'))

        then:
        1 * fintEvents.deleteTempQueues() >> true
        response.andExpect(status().isOk())
                .andExpect(jsonPath('$').value(equalTo(true)))
    }

    def "DELETE the content of all queues"() {
        given:
        def queue = Mock(RBlockingQueue)

        when:
        def response = mockMvc.perform(delete('/admin/clear/all'))

        then:
        1 * fintEvents.getQueues() >> ['my-queue']
        1 * fintEvents.getQueue('my-queue') >> queue
        1 * queue.clear()
        response.andExpect(status().isOk())
    }

    def "DELETE content of downstream queue for orgId"() {
        given:
        def queue = Mock(RBlockingQueue)

        when:
        def response = mockMvc.perform(delete('/admin/clear/downstream').header(Constants.HEADER_ORGID, 'rogfk.no'))

        then:
        1 * fintEvents.getDownstream('rogfk.no') >> queue
        1 * queue.clear()
        response.andExpect(status().isOk())
    }

    def "DELETE content of upstream queue for orgId"() {
        given:
        def queue = Mock(RBlockingQueue)

        when:
        def response = mockMvc.perform(delete('/admin/clear/upstream').header(Constants.HEADER_ORGID, 'rogfk.no'))

        then:
        1 * fintEvents.getUpstream('rogfk.no') >> queue
        1 * queue.clear()
        response.andExpect(status().isOk())
    }

    def "POST new orgId"() {
        when:
        def response = mockMvc.perform(post('/admin/orgIds/123'))

        then:
        1 * fintEvents.registerDownstreamListener(DownstreamSubscriber, '123')
        response.andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.header().string(HttpHeaders.LOCATION, equalTo('http://localhost/admin/orgIds/123')))
    }

    def "POST new orgId, return bad request if orgId is already registered"() {
        when:
        def response = mockMvc.perform(post('/admin/orgIds/123'))

        then:
        1 * adminService.isRegistered('123') >> true
        response.andExpect(status().isBadRequest())
    }

    def "GET all registered orgIds"() {
        when:
        def response = mockMvc.perform(get('/admin/orgIds'))

        then:
        1 * adminService.getOrgIds() >> ['123': 123456L, '234': 234567L]
        response.andExpect(status().isOk())
                .andExpect(jsonPath('$', hasSize(2)))
                .andExpect(jsonPath('$[0].orgId').value(equalTo('123')))
                .andExpect(jsonPath('$[0].registered').value(equalTo(123456)))
                .andExpect(jsonPath('$[1].orgId').value(equalTo('234')))
                .andExpect(jsonPath('$[1].registered').value(equalTo(234567)))
    }

    def "GET registered orgId"() {
        when:
        def response = mockMvc.perform(get('/admin/orgIds/123'))

        then:
        1 * adminService.isRegistered('123') >> true
        1 * adminService.getTimestamp('123') >> 123456L
        response.andExpect(status().isOk())
                .andExpect(jsonPath('$.orgId').value(equalTo('123')))
                .andExpect(jsonPath('$.registered').value(equalTo(123456)))
    }

    def "GET non existing orgId, return not found"() {
        when:
        def response = mockMvc.perform(get('/admin/orgIds/123'))

        then:
        response.andExpect(status().isNotFound())
    }
}
